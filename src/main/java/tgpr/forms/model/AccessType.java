package tgpr.forms.model;

import java.util.Arrays;

public enum AccessType {
    User, Editor;

    public static AccessType valueOfIgnoreCase(String str) {
        return Arrays.stream(values()).filter(v -> v.name().equalsIgnoreCase(str)).findFirst().orElse(null);
    }
}
