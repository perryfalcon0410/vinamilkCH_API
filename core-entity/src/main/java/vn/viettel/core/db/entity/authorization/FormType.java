package vn.viettel.core.db.entity.authorization;

import java.util.Arrays;

public enum FormType {
    MODULE(0, "module"),
    COMPONENT(1, "component"),
    SUBCOMPONENT(2, "subcomponent"),
    ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    FormType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int value) {
        return Arrays.stream(values())
                .filter(card -> card.type == value)
                .findFirst().get().value;
    }
}
