package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Answer extends Model {
    public enum Fields {
        Instance, Question, Value
    }

    public Answer() {
    }

    public Answer(Instance instance, Question question, String value) {
        this.instanceId = instance.getId();
        this.questionId = question.getId();
        this.value = value;
    }

    private int instanceId;

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public Instance getInstance() {
        return Instance.getByKey(instanceId);
    }

    private int questionId;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Question getQuestion() {
        return Question.getByKey(questionId);
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return instanceId == answer.instanceId && questionId == answer.questionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, questionId);
    }

    @Override
    public String toString() {
        return "Answer[" +
                "instanceId=" + instanceId +
                ", questionId=" + questionId +
                ", value=" + value +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        instanceId = rs.getInt("instance");
        questionId = rs.getInt("question");
        value = rs.getString("value");
    }

    @Override
    public void reload() {
        reload("select * from answers where instance=:instance and question=:question",
                new Params("instance", instanceId).add("question", questionId));
    }

    public static Answer getByKey(int instanceId, int questionId) {
        return queryOne(Answer.class, "select * from answers where instance=:instance and question=:question",
                new Params("instance", instanceId).add("question", questionId));
    }

    public static List<Answer> getAll() {
        return queryList(Answer.class, "select * from answers");
    }

    public Answer save() {
        int c;
        Answer obj = getByKey(instanceId, questionId);
        String sql;
        var params = new Params()
                .add("instance", instanceId)
                .add("question", questionId)
                .add("value", value);
        if (obj == null) {
            sql = "insert into answers (instance,question,value) " +
                    "values (:instance,:question,:value)";
            insert(sql, params);
        } else {
            sql = "update answers set value=:value " +
                    "where instance=:instance and question=:question";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        return this;
    }

    public void delete() {
        int c = execute("delete from answers where instance=:instance and question=:question",
                new Params("instance", instanceId).add("question", questionId));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public boolean isValid() {
        // valide si la question n'est pas requise ou si la valeur n'est pas vide
        return !getQuestion().getRequired() || value != null && !value.isBlank();
    }
}
