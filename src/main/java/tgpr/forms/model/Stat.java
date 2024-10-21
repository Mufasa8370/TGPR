package tgpr.forms.model;

public class Stat {
    private String value;
    private int count;
    private String ratio;

    public String getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
