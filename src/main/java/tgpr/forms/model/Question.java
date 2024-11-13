package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static tgpr.forms.controller.AnalyzeController.cutText;

public class Question extends Model {
    public enum Fields {
        Id, Form, Idx, Title, Description, Type, Required, OptionList
    }

    public enum Type {
        Short, Long, Radio, Check, Combo, Date, Email;

        public static Type valueOfIgnoreCase(String str) {
            return Arrays.stream(values()).filter(v -> v.name().equalsIgnoreCase(str)).findFirst().orElse(null);
        }

        public boolean requiresOptionList() {
            return this == Radio || this == Check || this == Combo;
        }
    }

    public Question() {
        type = Type.Short;
    }

    public Question(Form form, int idx, String title, Type type, boolean required) {
        this.formId = form.getId();
        this.idx = idx;
        this.title = title;
        this.type = type;
        this.required = required;
    }

    public Question(Form form, int idx, String title, String description, Type type, boolean required, int optionListId) {
        this.formId = form.getId();
        this.idx = idx;
        this.title = title;
        this.description = description;
        this.type = type;
        this.required = required;
        this.optionListId = optionListId;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Answer> getAnswers() {
        return queryList(Answer.class, "select * from answers where question=:id",
                new Params("id", id));
    }

    private int formId;

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Form getForm() {
        return Form.getByKey(formId);
    }

    private int idx;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public String getTitleForAnalyze() {
        return cutText(title, 25);
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

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private boolean required;

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private Integer optionListId;

    public Integer getOptionListId() {
        return optionListId;
    }

    public void setOptionListId(Integer optionListId) {
        this.optionListId = optionListId == null || optionListId == 0 ? null : optionListId;
    }

    public OptionList getOptionList() {
        if (optionListId == null)
            return null;
        return OptionList.getByKey(optionListId);
    }
    public String getOptionListForViewForm() {
        if (optionListId == null)
            return "";
        return OptionList.getByKey(optionListId).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Question[" +
                "id=" + id +
                ", formId=" + formId +
                ", idx=" + idx +
                ", title=" + title +
                ", description=" + description +
                ", type=" + type +
                ", required=" + required +
                ", optionListId=" + optionListId +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        formId = rs.getInt("form");
        idx = rs.getInt("idx");
        title = rs.getString("title");
        description = rs.getString("description");
        type = Type.valueOfIgnoreCase(rs.getString("type"));
        required = rs.getBoolean("required");
        optionListId = rs.getObject("option_list", Integer.class);
    }

    @Override
    public void reload() {
        reload("select * from questions where id=:id",
                new Params("id", id));
    }

    public static Question getByKey(int id) {
        return queryOne(Question.class, "select * from questions where id=:id",
                new Params("id", id));
    }

    public static List<Question> getAll() {
        return queryList(Question.class, "select * from questions");
    }

    public Question save() {
        int c;
        Question obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("form", formId)
                .add("idx", idx)
                .add("title", title)
                .add("description", description)
                .add("type", type.name().toLowerCase())
                .add("required", required)
                .add("option_list", optionListId);
        if (obj == null) {
            sql = "insert into questions (form,idx,title,description,type,required,option_list) " +
                    "values (:form,:idx,:title,:description,:type,:required,:option_list)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update questions set form=:form," +
                    "idx=:idx," +
                    "title=:title," +
                    "description=:description," +
                    "type=:type," +
                    "required=:required," +
                    "option_list=:option_list " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        reload();   // reload to get default values
        return this;
    }

    public void delete() {
        int c = execute("delete from questions where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public List<ValueStat> getStats() {
        if (type.requiresOptionList()) {
            return queryList(ValueStat.class, """
                            select ov.label as value, count(*) as count,
                                (select count(*) from instances where form=:form and completed is not null) as instance_count
                            from answers a
                                join option_values ov on a.value=ov.idx
                                join instances i on a.instance=i.id
                            where a.question=:id
                            and ov.option_list=:optionList
                            and i.completed is not null
                            group by value
                            order by count desc, ov.idx
                            """,
                    new Params("id", getId())
                            .add("optionList", getOptionListId())
                            .add("form", getFormId()));
        } else {
            return queryList(ValueStat.class, """
                            select if(value is not null and value != '', value, '--- vide ---') as value, count(*) as count,
                                (select count(*) from instances where form=:form and completed is not null) as instance_count
                            from answers a
                                join instances i on a.instance=i.id
                            where question=:id
                            and i.completed is not null
                            group by value
                            order by count desc, value
                            """,
                    new Params("id", getId())
                            .add("form", getFormId()));
        }
    }

    public String getAnswerValue(Instance instance) {
        var answer = instance.getAnswer(this);
        return answer == null ? null : answer.getValue();
    }

    public String getAnswerValueAsString(Instance instance) {
        var value = getAnswerValue(instance);
        if (getType().requiresOptionList()) {
            return value == null ? null : getOptionList().getValue(Integer.parseInt(value)).getLabel();
        }
        return value;
    }

    public List<OptionValue> getAnswerValues(Instance instance) {
        return queryList(OptionValue.class, """
                        select ov.*
                        from answers a
                            join option_values ov on a.value=ov.idx
                        where a.instance=:instance
                        and a.question=:question
                        and ov.option_list=:optionList
                        order by ov.label
                        """,
                new Params("instance", instance.getId())
                        .add("question", getId())
                        .add("optionList", getOptionListId()));
    }

    public void saveAnswer(Instance instance, List<Integer> indexes) {
        execute("delete from answers where instance=:instance and question=:question",
                new Params("instance", instance.getId())
                        .add("question", getId()));
        int i = 1;
        for (int idx : indexes) {
            insert("insert into answers (instance,question,idx,value) values (:instance,:question,:idx,:value)",
                    new Params("instance", instance.getId())
                            .add("question", getId())
                            .add("idx", i)
                            .add("value", idx));
            i++;
        }
    }
}
