package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class OptionList extends Model {

    public enum Fields {
        Id, Name, Values, Owner;
    }
    public OptionList() {
    }

    public OptionList(String name) {
        this.name = name;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OptionValue> getOptionValues() {
        return queryList(OptionValue.class, "select * from option_values where option_list=:id order by idx",
                new Params("id", id));
    }

    public int getNumberOfValues() {
        return getOptionValues().size();
    }

    public List<Question> getQuestions() {
        return queryList(Question.class, "select * from questions where option_list=:id",
                new Params("id", id));
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer ownerId;

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public User getOwner() {
        return ownerId == null ? null : User.getByKey(ownerId);
    }

    public String getOwnerForManage() {
        return ownerId == null ? "System" : User.getByKey(ownerId).getName();
    }

    public boolean isSystem() {
        return id > 0 && (ownerId == null || ownerId <= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionList optionList = (OptionList) o;
        return id == optionList.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        name = rs.getString("name");
        ownerId = rs.getObject("owner", Integer.class);
    }

    @Override
    public void reload() {
        reload("select * from option_lists where id=:id",
                new Params("id", id));
    }

    public static OptionList getByKey(int id) {
        return queryOne(OptionList.class, "select * from option_lists where id=:id",
                new Params("id", id));
    }

    public static List<OptionList> getAll() {
        return queryList(OptionList.class, "select * from option_lists order by name");
    }

    public static List<OptionList> getForUser(User user) {
        return queryList(OptionList.class, """
                        select * from option_lists
                        where owner=:owner
                        or owner is null
                        or :owner in (select id from users where role='admin')
                        order by name
                        """,
                new Params("owner", user.getId()));
    }

    public OptionList save() {
        int c;
        OptionList obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("name", name)
                .add("owner", ownerId);
        if (obj == null) {
            sql = "insert into option_lists (name, owner) " +
                    "values (:name, :owner)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update option_lists set name=:name, owner=:owner " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        return this;
    }

    public void delete() {
        int c = execute("delete from option_lists where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    private final OptionValue emptyValue = new OptionValue(this, -1, "-- Please choose an option --");

    public OptionValue getEmptyValue() {
        return emptyValue;
    }

    private final OptionValue requiredValue = new OptionValue(this, -1, "");

    public OptionValue getRequiredValue() {
        return emptyValue;
    }

    public OptionValue getValue(int idx) {
        return queryOne(OptionValue.class, """
                        select * from option_values
                        where option_list=:list
                        and idx=:idx
                        """,
                new Params("list", id)
                        .add("idx", idx));
    }

    public static OptionList getByNameAndUser(String name, User user) {
        return queryOne(OptionList.class, """
                        select *
                        from option_lists
                        where name=:name
                        and (owner is null or owner=:user)
                        """,
                new Params("name", name).add("user", user.getId()));
    }

    public boolean hasValue(OptionValue value) {
        return queryOne(OptionValue.class, """
                        select *
                        from option_values
                        where option_list=:list
                        and idx=:idx
                        """,
                new Params("list", id)
                        .add("idx", value.getIdx())) != null;
    }

    public void addValue(OptionValue value) {
        if (hasValue(value)) return;
        value.setOptionListId(id);
        value.save();
    }

    public void addValues(List<OptionValue> values) {
        for (OptionValue value : values)
            addValue(value);
    }

    public void setValues(List<OptionValue> values) {
        deleteAllValues();
        for (OptionValue value : values)
            addValue(value);
    }

    public void deleteAllValues() {
        execute("delete from option_values where option_list=:list",
                new Params("list", id));
    }

    public void reorderValues(List<OptionValue> values) {
        deleteAllValues();
        int i = 1;
        for (var value : values) {
            if (value.getOptionListId() <= 0)
                value.setOptionListId(id);
            value.setIdx(i++);
            value.save();
        }
    }

    public OptionList duplicate(User forUser) {
        Assert.notNull(forUser, "User is required");
        String newName = name + " (copy)";
        int i = 1;
        while (getByNameAndUser(newName, forUser) != null) {
            newName = name + " (copy " + i + ")";
            i++;
        }
        OptionList newList = new OptionList(newName);
        newList.setOwnerId(forUser.getId());
        newList.save();
        for (OptionValue value : getOptionValues()) {
            OptionValue newValue = new OptionValue(newList, value.getIdx(), value.getLabel());
            newValue.save();
        }
        return newList;
    }

    public boolean isUsed() {
        return !getQuestions().isEmpty();
    }
}
