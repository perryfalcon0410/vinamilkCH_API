package vn.viettel.core.db.entity.sale;

import java.util.Arrays;

public enum OrderType {
    OFFLINE(0, "offline"),
    ONLINE(1, "online"),
    ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    OrderType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int type) {
        return Arrays.stream(values())
                .filter(orderType -> orderType.type == type)
                .findFirst().get().value;
    }
}
