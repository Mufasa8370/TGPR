package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UserFormAccess extends Model {
    public enum Fields {
        User, Form, AccessType
    }

    public UserFormAccess() {
    }

    public UserFormAccess(User user, Form form, AccessType accessType) {
        this.userId = user.getId();
		this.formId = form.getId();
		this.accessType = accessType;
    }

    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return User.getByKey(userId);
    }

    protected int formId;

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Form getForm() {
        return Form.getByKey(formId);
    }

    protected AccessType accessType;

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFormAccess userFormAccess = (UserFormAccess) o;
        return formId == userFormAccess.formId && userId == userFormAccess.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(formId, userId);
    }

    @Override
    public String toString() {
        return "UserFormAccess[" +
			"userId=" + userId +
			", formId=" + formId +
			", accessType=" + accessType +
			"]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        userId = rs.getInt("user");
        formId = rs.getInt("form");
        accessType = AccessType.valueOfIgnoreCase(rs.getString("access_type"));
    }

    @Override
    public void reload() {
        reload("select * from user_form_accesses where form=:form and user=:user",
            new Params("form", formId).add("user", userId));
    }

    public static UserFormAccess getByKey(int formId, int userId) {
        return queryOne(UserFormAccess.class, "select * from user_form_accesses where form=:form and user=:user",
            new Params("form", formId).add("user", userId));
    }

    public static List<UserFormAccess> getAll() {
        return queryList(UserFormAccess.class, "select * from user_form_accesses");
    }

    public UserFormAccess save() {
        int c;
        UserFormAccess obj = getByKey(formId, userId);
        String sql;
        if (obj == null)
            sql = "insert into user_form_accesses (user,form,access_type) " +
				"values (:user,:form,:access_type)";
        else
            sql = "update user_form_accesses set access_type=:access_type " +
				"where form=:form and user=:user";
        var params = new Params()
			.add("user", userId)
			.add("form", formId)
			.add("access_type", accessType.name().toLowerCase());
        c = execute(sql, params);
        Assert.isTrue(c == 1, "Something went wrong");
        return this;
    }

    public void delete() {
        int c = execute("delete from user_form_accesses where form=:form and user=:user",
			new Params("form", formId).add("user", userId));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public void toggle() {
        accessType = accessType == AccessType.User ? AccessType.Editor : AccessType.User;
        save();
    }
}
