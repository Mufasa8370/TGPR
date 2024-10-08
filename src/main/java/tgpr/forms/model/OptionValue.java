package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class OptionValue extends Model {
    public enum Fields {
        OptionList, Idx, Label
    }

    public OptionValue() {
    }

    public OptionValue(OptionList optionList, int idx, String label) {
        this.optionListId = optionList != null ? optionList.getId() : 0;
		this.idx = idx;
		this.label = label;
    }

    private int optionListId;

    public int getOptionListId() {
        return optionListId;
    }

    public void setOptionListId(int optionListId) {
        this.optionListId = optionListId;
    }

    public OptionList getOptionList() {
        return OptionList.getByKey(optionListId);
    }

    private int idx;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionValue optionValue = (OptionValue) o;
        return idx == optionValue.idx && optionListId == optionValue.optionListId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx, optionListId);
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        optionListId = rs.getInt("option_list");
        idx = rs.getInt("idx");
        label = rs.getString("label");
    }

    @Override
    public void reload() {
        reload("select * from option_values where idx=:idx and option_list=:option_list",
            new Params("idx", idx).add("option_list", optionListId));
    }

    public static OptionValue getByKey(int idx, int optionListId) {
        return queryOne(OptionValue.class, "select * from option_values where idx=:idx and option_list=:option_list",
            new Params("idx", idx).add("option_list", optionListId));
    }

    public static List<OptionValue> getAll() {
        return queryList(OptionValue.class, "select * from option_values");
    }

    public OptionValue save() {
        int c;
        OptionValue obj = getByKey(idx, optionListId);
        String sql;
        if (obj == null)
            sql = "insert into option_values (option_list,idx,label) " +
				"values (:option_list,:idx,:label)";
        else
            sql = "update option_values set label=:label " +
				"where idx=:idx and option_list=:option_list";
        var params = new Params()
			.add("option_list", optionListId)
			.add("idx", idx)
			.add("label", label);
        c = execute(sql, params);
        Assert.isTrue(c == 1, "Something went wrong");
        return this;
    }

    public void delete() {
        int c = execute("delete from option_values where idx=:idx and option_list=:option_list",
			new Params("idx", idx).add("option_list", optionListId));
        Assert.isTrue(c == 1, "Something went wrong");
    }
}
