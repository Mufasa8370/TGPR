package tgpr.forms.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DistListUser extends Model {
    public enum Fields {
        DistList, User
    }

    public DistListUser() {
    }

    public DistListUser(DistList distList, User user) {
        this.distListId = distList.getId();
        this.userId = user.getId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistListUser distListUser = (DistListUser) o;
        return distListId == distListUser.distListId && userId == distListUser.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distListId, userId);
    }

    @Override
    public String toString() {
        return "DistListsUser[" +
                "distListId=" + distListId +
                ", userId=" + userId +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        distListId = rs.getInt("distlist");
        userId = rs.getInt("user");
    }

    @Override
    public void reload() {
        reload("select * from distlist_users where distlist=:distlist and user=:user",
                new Params("distlist", distListId).add("user", userId));
    }

    public static DistListUser getByKey(int distListId, int userId) {
        return queryOne(DistListUser.class, "select * from distlist_users where distlist=:distlist and user=:user",
                new Params("distlist", distListId).add("user", userId));
    }


    public static List<DistListUser> getAll() {
        return queryList(DistListUser.class, "select * from distlist_users");
    }

    public DistListUser save() {
        int c;
        DistListUser obj = getByKey(distListId, userId);
        String sql;
        if (obj == null)
            sql = "insert into distlist_users (distlist,user) " +
                    "values (:distlist,:user)";
        else
            return this;
        var params = new Params()
                .add("distlist", distListId)
                .add("user", userId);
        c = execute(sql, params);
        Assert.isTrue(c == 1, "Something went wrong");
        return this;
    }

    public void delete() {
        int c = execute("delete from distlist_users where distlist=:distlist and user=:user",
                new Params("distlist", distListId).add("user", userId));
        Assert.isTrue(c == 1, "Something went wrong");
    }
}
