package vn.viettel.core.db.entity.sale;

import java.util.Arrays;

public enum PaymentType {
    CASH(0, "cash"),
    CARD(1, "card"),
            ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    PaymentType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int type) {
        return Arrays.stream(values())
                .filter(paymentType -> paymentType.type == type)
                .findFirst().get().value;
    }
}
