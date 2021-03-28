package vn.viettel.core.db.entity.sale;

import java.util.Arrays;

public enum DeliveryType {
    AT_HOME(0, "at home"),
    AT_SHOP(1, "at shop"),
            ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    DeliveryType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int type) {
        return Arrays.stream(values())
                .filter(deliveryType -> deliveryType.type == type)
                .findFirst().get().value;
    }
}
