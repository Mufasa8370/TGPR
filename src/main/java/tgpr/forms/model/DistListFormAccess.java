package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DistListFormAccess extends Model {
    public enum Fields {
        DistList, Form, AccessType
    }

    public DistListFormAccess() {
    }

    public DistListFormAccess(DistList distList, Form form, AccessType accessType) {
        this.distListId = distList.getId();
		this.formId = form.getId();
		this.accessType = accessType;
    }

    private int distListId;

    public int getDistListId() {
        return distListId;
    }

    public void setDistListId(int distListId) {
        this.distListId = distListId;
    }


    public DistList getDistList() {
        return DistList.getByKey(distListId);
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
        DistListFormAccess distListFormAccess = (DistListFormAccess) o;
        return distListId == distListFormAccess.distListId && formId == distListFormAccess.formId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distListId, formId);
    }

    @Override
    public String toString() {
        return "DistListFormAccess[" +
			"distListId=" + distListId +
			", formId=" + formId +
			", accessType=" + accessType +
			"]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        distListId = rs.getInt("distlist");
        formId = rs.getInt("form");
        accessType = AccessType.valueOfIgnoreCase(rs.getString("access_type"));
    }

    @Override
    public void reload() {
        reload("select * from distlist_form_accesses where distlist=:distlist and form=:form",
            new Params("distlist", distListId).add("form", formId));
    }

    public static DistListFormAccess getByKey(int distListId, int formId) {
        return queryOne(DistListFormAccess.class, "select * from distlist_form_accesses where distlist=:distlist and form=:form",
            new Params("distlist", distListId).add("form", formId));
    }

    public static List<DistListFormAccess> getAll() {
        return queryList(DistListFormAccess.class, "select * from distlist_form_accesses");
    }

    public DistListFormAccess save() {
        int c;
        DistListFormAccess obj = getByKey(distListId, formId);
        String sql;
        if (obj == null)
            sql = "insert into distlist_form_accesses (distlist,form,access_type) " +
				"values (:distlist,:form,:access_type)";
        else
            sql = "update distlist_form_accesses set access_type=:access_type " +
				"where distlist=:distlist and form=:form";
        var params = new Params()
			.add("distlist", distListId)
			.add("form", formId)
			.add("access_type", accessType.name().toLowerCase());
        c = execute(sql, params);
        Assert.isTrue(c == 1, "Something went wrong");
        return this;
    }

    public void delete() {
        int c = execute("delete from distlist_form_accesses where distlist=:distlist and form=:form",
			new Params("distlist", distListId).add("form", formId));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public void toggle() {
        accessType = accessType == AccessType.User ? AccessType.Editor : AccessType.User;
        save();
    }
}
