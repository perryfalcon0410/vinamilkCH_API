package vn.viettel.authorization.entities;

import java.util.Arrays;

public enum ShowStatus {
    INVISIBLE(0, "invisible"),
    ENABLE(1, "enable"),
    DISABLE(2, "disable"),
    ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    ShowStatus(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int value) {
        return Arrays.stream(values())
                .filter(card -> card.type == value)
                .findFirst().get().value;
    }
}
