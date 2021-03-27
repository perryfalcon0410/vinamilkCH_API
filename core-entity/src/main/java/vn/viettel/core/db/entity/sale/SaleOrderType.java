package vn.viettel.core.db.entity.sale;

import java.util.Arrays;

public enum SaleOrderType {
    SALE(0, "sale"),
    RETURN(1, "return"),
            ;
    private final int type;
    private final String value;

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    SaleOrderType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueOf(int value) {
        return Arrays.stream(values())
                .filter(card -> card.type == value)
                .findFirst().get().value;
    }
}
