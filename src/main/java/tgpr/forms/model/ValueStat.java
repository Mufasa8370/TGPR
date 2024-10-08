package tgpr.forms.model;

import tgpr.framework.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ValueStat extends Model {
    private String value;

    public String getValue() {
        return value;
    }

    private int count;

    public int getCount() {
        return count;
    }

    private int instanceCount;

    public int getInstanceCount() {
        return instanceCount;
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        value = rs.getString("value");
        count = rs.getInt("count");
        instanceCount = rs.getInt("instance_count");
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "ValueStat{" +
                "value='" + value + '\'' +
                ", count=" + count +
                ", instanceCount=" + instanceCount +
                '}';
    }
}
