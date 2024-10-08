package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DistList extends Model {
    public enum Fields {
        Id, Name, Description, Values, Owner
    }

    public DistList() {
    }

    public DistList(String name) {
        this.name = name;
    }

    public DistList(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DistListUser> getDistListsUsers() {
        return queryList(DistListUser.class, "select * from distlist_users where distlist=:id",
                new Params("id", id));
    }

    public List<DistListFormAccess> getDistListFormAccesses() {
        return queryList(DistListFormAccess.class, "select * from distlist_form_accesses where distlist=:id",
                new Params("id", id));
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistList distList = (DistList) o;
        return id == distList.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        name = rs.getString("name");
        description = rs.getString("description");
        ownerId = rs.getInt("owner");
    }

    @Override
    public void reload() {
        reload("select * from distlists where id=:id",
                new Params("id", id));
    }

    public static DistList getByKey(int id) {
        return queryOne(DistList.class, "select * from distlists where id=:id",
                new Params("id", id));
    }

    public static List<DistList> getAll() {
        return queryList(DistList.class, "select * from distlists");
    }

    public DistList save() {
        int c;
        DistList obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("owner", ownerId);
        if (obj == null) {
            sql = "insert into distlists (name,description,owner) " +
                    "values (:name,:description,:owner)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update distlists set name=:name," +
                    "description=:description, " +
                    "owner=:owner " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        reload();   // reload to get default values
        return this;
    }

    public void delete() {
        int c = execute("delete from distlists where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public static List<DistList> getForUser(User user) {
        return queryList(DistList.class, """
                        select *
                        from distlists
                        where owner=:owner or
                        (select role from users where id=:userId)='admin'
                        """,
                new Params("owner", user.getId()).add("userId", user.getId()));
    }

    public List<User> getUsers() {
        return queryList(User.class, """
                select *
                from users u
                where u.id in (select user from distlist_users where distlist=:distlist)
                order by u.full_name
                """, new Params("distlist", id));
    }

    public List<User> getOtherUsers() {
        return queryList(User.class, """
                select *
                from users u
                where u.id not in (select user from distlist_users where distlist=:distlist)
                and role!='guest'
                order by u.full_name
                """, new Params("distlist", id).add("userId", getOwnerId()));
    }

    public boolean hasUser(User user) {
        return queryOne(DistListUser.class, """
                        select *
                        from distlist_users
                        where distlist=:distlist
                        and user=:user
                        """,
                new Params("distlist", id)
                        .add("user", user.getId())) != null;
    }

    public void addUser(User user) {
        if (hasUser(user)) return;
        execute("insert into distlist_users (distlist,user) values (:distlist,:user)",
                new Params("distlist", id).add("user", user.getId()));
    }

    public void addUsers(List<User> users) {
        for (User user : users)
            addUser(user);
    }

    public void setUsers(List<User> users) {
        deleteAllUsers();
        for (User user : users)
            addUser(user);
    }

    public void deleteAllUsers() {
        execute("delete from distlist_users where distlist=:distlist",
                new Params("distlist", id));
    }

    public static DistList getByNameAndUser(String name, User user) {
        return queryOne(DistList.class, """
                        select *
                        from distlists
                        where name=:name
                        and owner=:owner
                        """,
                new Params("name", name).add("owner", user.getId()));
    }
}
