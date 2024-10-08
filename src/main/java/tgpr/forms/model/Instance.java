package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Instance extends Model {
    public enum Fields {
        Id, Form, User, Started, Completed
    }

    public Instance() {
    }

    public Instance(Form form, User user) {
        this.formId = form.getId();
        this.userId = user.getId();
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Answer> getAnswers() {
        return queryList(Answer.class, "select * from answers where instance=:id order by question",
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

    private LocalDateTime started;

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    private LocalDateTime completed;

    public LocalDateTime getCompleted() {
        return completed;
    }

    public boolean isCompleted() {
        return completed != null;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed = completed;
    }

    public void complete() {
        setCompleted(LocalDateTime.now());
        save();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return id == instance.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Instance[" +
                "id=" + id +
                ", formId=" + formId +
                ", userId=" + userId +
                ", started=" + started +
                ", completed=" + completed +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        formId = rs.getInt("form");
        userId = rs.getInt("user");
        started = rs.getObject("started", LocalDateTime.class);
        completed = rs.getObject("completed", LocalDateTime.class);
    }

    @Override
    public void reload() {
        reload("select * from instances where id=:id",
                new Params("id", id));
    }

    public static Instance getByKey(int id) {
        return queryOne(Instance.class, "select * from instances where id=:id",
                new Params("id", id));
    }

    public static List<Instance> getAll() {
        return queryList(Instance.class, "select * from instances");
    }

    public Instance save() {
        int c;
        Instance obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("form", formId)
                .add("user", userId)
                .add("started", started)
                .add("completed", completed);
        if (obj == null) {
            sql = "insert into instances (form,user,started,completed) " +
                    "values (:form,:user,:started,:completed)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update instances set form=:form," +
                    "user=:user," +
                    "started=:started," +
                    "completed=:completed " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        reload();   // reload to get default values
        return this;
    }

    public void delete() {
        int c = execute("delete from instances where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public Answer getAnswer(Question question) {
        return queryOne(Answer.class, """
                        select *
                        from answers
                        where instance=:instance
                        and question=:question
                        """,
                new Params("instance", id)
                        .add("question", question.getId()));
    }

    public Answer getOrCreateAnswer(Question question) {
        var answer = getAnswer(question);
        if (answer == null) {
            answer = new Answer(this, question, "");
            answer.save();
        }
        return answer;
    }

    public Answer getFirstInvalidAnswer() {
        var answers = getAnswers();
        for (var answer : answers) {
            if (!answer.isValid())
                return answer;
        }
        return null;
    }

    public void submit() {
        setCompleted(LocalDateTime.now());
        save();
    }

    public List<Answer> getAnswers(Question question) {
        return queryList(Answer.class, "select * from answers where instance=:id and question=:question",
                new Params("id", id).add("question", question.getId()));
    }
}
