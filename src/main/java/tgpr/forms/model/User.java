package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;
import static tgpr.framework.Tools.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User extends Model {
    public enum Fields {
        Id, FullName, Email, Password, ConfirmPassword, OldPassword, Role
    }

    public enum Role {
        User, Admin, Guest;

        public static Role valueOfIgnoreCase(String str) {
            return Arrays.stream(values()).filter(v -> v.name().equalsIgnoreCase(str)).findFirst().orElse(null);
        }
    }

    public User() {
    }

    public User(String fullName, String email, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DistListUser> getDistListsUsers() {
        return queryList(DistListUser.class, "select * from distlist_users where user=:id",
                new Params("id", id));
    }

    public List<Form> getForms() {
        return queryList(Form.class, "select * from forms where owner=:id",
                new Params("id", id));
    }

    public List<Instance> getInstances() {
        return queryList(Instance.class, "select * from instances where user=:id",
                new Params("id", id));
    }

    public List<UserFormAccess> getUserFormAccesses() {
        return queryList(UserFormAccess.class, "select * from user_form_accesses where user=:id",
                new Params("id", id));
    }

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return fullName;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.Admin;
    }

    public boolean isGuest() {
        return role == Role.Guest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        fullName = rs.getString("full_name");
        email = rs.getString("email");
        password = rs.getString("password");
        role = Role.valueOfIgnoreCase(rs.getString("role"));
    }

    @Override
    public void reload() {
        reload("select * from users where id=:id",
                new Params("id", id));
    }

    public static User getByKey(int id) {
        return queryOne(User.class, "select * from users where id=:id",
                new Params("id", id));
    }

    public static User getByEmail(String email) {
        return queryOne(User.class, "select * from users where email=:email",
                new Params("email", email));
    }

    public static User getByFullName(String fullName) {
        return queryOne(User.class, "select * from users where full_name=:fullName",
                new Params("fullName", fullName));
    }

    public static List<User> getAll() {
        return queryList(User.class, "select * from users where role!='guest' order by full_name");
    }

    public static List<User> getAllUsersExceptSelf(User user) {
        return queryList(User.class, """
                select *
                from users
                where role='user'
                and id<>:id
                order by full_name
                """, new Params("id", user.id));
    }

    public User save() {
        int c;
        User obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("full_name", fullName)
                .add("email", email)
                .add("password", password)
                .add("role", role.name().toLowerCase());
        if (obj == null) {
            sql = "insert into users (full_name,email,password,role) " +
                    "values (:full_name,:email,:password,:role)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update users set full_name=:full_name," +
                    "email=:email," +
                    "password=:password," +
                    "role=:role " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        return this;
    }

    public void delete() {
        int c = execute("delete from users where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public static User checkCredentials(String mail, String password) {
        var user = User.getByEmail(mail);
        if (user != null && user.password.equals(hash(password)))
            return user;
        return null;
    }

    public Form addForm(String title) {
        var form = new Form(title, this);
        form.save();
        return form;
    }

    public Form addForm(String title, String description, boolean isPublic) {
        var form = new Form(title, description, this, isPublic);
        form.save();
        return form;
    }

    public List<Form> getMyForms(String filter, int start, int count) {
        return Form.getForUser(this, filter, start, count);
    }

    public int countMyForms(String filter) {
        return Form.countForUser(this, filter);
    }

    public Form getFormByTitle(String title) {
        return Form.getByTitleAndUser(title, this);
    }

    public OptionList getOptionListByName(String name) {
        return OptionList.getByNameAndUser(name, this);
    }

    public DistList getDistListByName(String name) {
        return DistList.getByNameAndUser(name, this);
    }

    public List<OptionList> getOptionLists() {
        return OptionList.getForUser(this);
    }

    public void deleteOpenInstances() {
        execute("delete from instances where user=:id and completed is null",
                new Params("id", id));
    }
}