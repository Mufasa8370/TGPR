package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;
import tgpr.framework.SortOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Form extends Model {
    public enum Fields {
        Id, Title, Description, Owner, IsPublic;
    }

    public Form() {
    }

    public Form(String title, User owner) {
        this.title = title;
        this.ownerId = owner.getId();
    }

    public Form(String title, String description, User owner, boolean isPublic) {
        this.title = title;
        this.description = description;
        this.ownerId = owner.getId();
        this.isPublic = isPublic;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DistListFormAccess> getDistListFormAccesses() {
        return queryList(DistListFormAccess.class, "select * from distlist_form_accesses where form=:id",
                new Params("id", id));
    }

    public List<Instance> getInstances() {
        return queryList(Instance.class, "select * from instances where form=:id",
                new Params("id", id));
    }

    public List<Question> getQuestions() {
        return queryList(Question.class, "select * from questions where form=:id order by idx",
                new Params("id", id));
    }

    public List<UserFormAccess> getUserFormAccesses() {
        return queryList(UserFormAccess.class, "select * from user_form_accesses where form=:id",
                new Params("id", id));
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int ownerId;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public User getOwner() {
        return User.getByKey(ownerId);
    }

    private boolean isPublic;

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Form form = (Form) o;
        return id == form.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Form[" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", ownerId=" + ownerId +
                ", isPublic=" + isPublic +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        title = rs.getString("title");
        description = rs.getString("description");
        ownerId = rs.getInt("owner");
        isPublic = rs.getBoolean("is_public");
    }

    @Override
    public void reload() {
        reload("select * from forms where id=:id",
                new Params("id", id));
    }

    public static Form getByKey(int id) {
        return queryOne(Form.class, "select * from forms where id=:id",
                new Params("id", id));
    }

    public static List<Form> getAll() {
        return queryList(Form.class, "select * from forms");
    }

    public static List<Form> getFiltered(String filterText, Fields sortField, SortOrder sortOrder, User loggedUser) {
        String filter = '%' + filterText + '%';
        Params params = new Params("filter", filter);
        String sql = """
                select * from forms f
                where (title like :filter or description like :filter)
                and exists(select * from questions where form=f.id)
                """;
        if (!loggedUser.isAdmin()) {
            sql += " and owner=:user";
            params.add("user", loggedUser.getId());
        }
        sql += " order by " + sortField.name() + " " + (sortOrder == SortOrder.Ascending ? "asc" : "desc");
        return queryList(Form.class, sql, params);
    }

    public Form save() {
        int c;
        Form obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("title", title)
                .add("description", description)
                .add("owner", ownerId)
                .add("is_public", isPublic);
        if (obj == null) {
            sql = "insert into forms (title,description,owner,is_public) " +
                    "values (:title,:description,:owner,:is_public)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update forms set title=:title," +
                    "description=:description," +
                    "owner=:owner," +
                    "is_public=:is_public " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");

            if (isPublic && !obj.isPublic)
                deleteAllUserAccesses();
        }
        reload();   // reload to get default values
        return this;
    }

    public void delete() {
        int c = execute("delete from forms where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public Instance getMostRecentInstance(User user) {
        // si l'utilisateur est un invité, on ne peut pas récupérer sa dernière instance
        if (user.isGuest())
            return null;
        return queryOne(Instance.class,
                """
                        select * from instances
                        where form=:form
                          and user=:user
                        order by started desc
                        limit 1
                        """,
                new Params("form", id)
                        .add("user", user.getId()));
    }

    public Instance createInstance(User user) {
        var instance = new Instance(this, user);
        instance.setStarted(LocalDateTime.now());
        instance.save();
        return instance;
    }

    public Instance getCurrentOrCreateInstance(User user) {
        var instance = getMostRecentInstance(user);
        if (instance == null || instance.isCompleted()) {
            instance = createInstance(user);
        }
        return instance;
    }

    private final static String GET_FOR_USER_SQL = """
            from forms
            where (
                is_public=1 or
                owner=:userid or
                (select role from users where id=:userid)='admin' or
                id in (select form from user_form_accesses where user=:userid) or
                id in (select form from distlist_form_accesses la
                       join distlist_users lu on la.distlist=lu.distlist
                       where lu.user=:userid)
            ) and (
                owner in (select id from users where full_name like :filter) or
                title like :filter or
                description like :filter or
                id in (
                    select form
                    from questions
                    where title like :filter or
                    description like :filter
                )
            )
            order by title
            """;

    public static List<Form> getForUser(User user, String filter, int start, int count) {
        return queryList(Form.class, "select * " + GET_FOR_USER_SQL + " limit :start, :count",
                new Params("userid", user.getId())
                        .add("filter", "%" + filter + "%")
                        .add("start", start)
                        .add("count", count)
        );
    }


    public static int countForUser(User user, String filter) {
        var count = queryScalar(Integer.class, "select count(*) " + GET_FOR_USER_SQL,
                new Params("userid", user.getId())
                        .add("filter", "%" + filter + "%")
        );
        return count == null ? 0 : count;
    }

    public static Form getByTitleAndUser(String title, User user) {
        return queryOne(Form.class, "select * from forms where title=:title and owner=:user",
                new Params("title", title).add("user", user.getId()));
    }

    public void reorderQuestions(List<Question> questions) {
        execute("update questions set idx=-idx where form=:form",
                new Params("form", id));
        int i = 1;
        for (var question : questions) {
            question.setIdx(i++);
            question.save();
        }
    }

    public int getNextIdx() {
        var idx = queryScalar(Integer.class, "select max(idx) from questions where form=:form",
                new Params("form", id));
        return idx == null ? 1 : idx + 1;
    }

    public List<Model> getAccesses() {
        var userAccesses = queryList(UserFormAccess.class, "select * from user_form_accesses where form=:form",
                new Params("form", id));
        var groupAccesses = queryList(DistListFormAccess.class, "select * from distlist_form_accesses where form=:form",
                new Params("form", id));
        List<Model> accesses = new ArrayList<>(userAccesses);
        accesses.addAll(groupAccesses);
        return accesses.stream().sorted(Comparator.comparing(this::getBeneficiaryName)).toList();
    }

    public String getBeneficiaryName(Model model) {
        if (model instanceof UserFormAccess ua)
            return ua.getUser().getName();
        else if (model instanceof DistListFormAccess da)
            return da.getDistList().getName();
        else if (model instanceof User u)
            return u.getName();
        else if (model instanceof DistList d)
            return d.getName();
        else
            throw new IllegalArgumentException("Wrong model type");
    }

    // gestion des permissions accessible uniquement si owner ou admin
    public boolean mayChangePermissions(User user) {
        return user.isAdmin() || user.getId() == ownerId;
    }

    public List<Model> getPotentialBeneficiaries() {
        var users = queryList(User.class, """
                        select *
                        from users u
                        where u.id not in (select user from user_form_accesses where form=:form)
                        and u.id != :owner
                        and u.role = 'user'
                        """,
                new Params("form", id).add("owner", ownerId));
        var lists = queryList(DistList.class, """
                        select *
                        from distlists d
                        where d.id not in (select distlist from distlist_form_accesses where form=:form)
                        and d.owner = :owner
                        """,
                new Params("form", id).add("owner", ownerId));
        List<Model> beneficiaries = new ArrayList<>(users);
        beneficiaries.addAll(lists);
        return beneficiaries.stream().sorted(Comparator.comparing(this::getBeneficiaryName)).toList();
    }

    public UserFormAccess addAccess(User beneficiary, AccessType accessType) {
        var access = new UserFormAccess((User) beneficiary, this, accessType);
        access.save();
        return access;
    }

    public DistListFormAccess addAccess(DistList beneficiary, AccessType accessType) {
        var access = new DistListFormAccess((DistList) beneficiary, this, accessType);
        access.save();
        return access;
    }

    public AccessType getAccessType(User user) {
        if (user.isAdmin() || user.getId() == getOwnerId())
            return AccessType.Editor;

        var userAccess = UserFormAccess.getByKey(id, user.getId());
        var listAccesses = queryList(DistListFormAccess.class, """
                select *
                from distlist_form_accesses a
                where form=:form
                and :user in (select user from distlist_users where distlist = a.distlist)
                """, new Params("form", id).add("user", user.getId()));

        if (userAccess == null && listAccesses.isEmpty())
            return null;

        if (userAccess != null && userAccess.getAccessType() == AccessType.Editor ||
                listAccesses.stream().anyMatch(a -> a.accessType == AccessType.Editor))
            return AccessType.Editor;

        return AccessType.User;
    }

    public boolean hasEditAccess(User user) {
        return getAccessType(user) == AccessType.Editor;
    }

    private void deleteAllUserAccesses() {
        execute("delete from user_form_accesses where form=:form and access_type='user'",
                new Params("form", id));
        execute("delete from distlist_form_accesses where form=:form and access_type='user'",
                new Params("form", id));
    }

    public boolean isShared() {
        return !queryList(UserFormAccess.class, "select * from user_form_accesses where form=:form",
                new Params("form", id)).isEmpty() ||
                !queryList(DistListFormAccess.class, "select * from distlist_form_accesses where form=:form",
                        new Params("form", id)).isEmpty();
    }

    public List<Instance> getCompletedInstances() {
        return queryList(Instance.class, "select * from instances where form=:form and completed is not null order by completed desc",
                new Params("form", id));
    }

    public boolean isUsed() {
        return !getInstances().isEmpty();
    }

    public void deleteAllInstances() {
        execute("delete from instances where form=:form",
                new Params("form", id));
    }

    public void deleteAllSubmittedInstances() {
        execute("delete from instances where form=:form and completed is not null",
                new Params("form", id));
    }

    public void togglePublic() {
        setIsPublic(!getIsPublic());
        save();
    }

    public void deleteAccesses() {
        var dx = DistListFormAccess.getAll();
        for( DistListFormAccess d:  dx ){
            if(d.getFormId() == id){
                d.delete();
            }
        }

        var ux = UserFormAccess.getAll();
        for(UserFormAccess u: ux){
            if(u.getFormId() == id && u.getUserId() != ownerId){
                u.delete();
            }
        }
    }

}

