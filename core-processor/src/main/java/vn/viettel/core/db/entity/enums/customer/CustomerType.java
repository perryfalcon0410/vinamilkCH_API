package vn.viettel.core.db.entity.enums.customer;

import java.util.Arrays;

public enum CustomerType {
    NORMAL_CUSTOMER(0, "KHACH HANG BINH THUONG"),
    LOYAL_CUSTOMER(1, "KHACH HANG THAN THIET"),
    ;
    private final int type;
    private final String value;

    private CustomerType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int typeCode() {return type;}
    public String customerTypeValue() {return value;}

    public static String getValueOf(int value) {
        return Arrays.stream(values())
                .filter(cus -> cus.type == value)
                .findFirst().get().toString();
    }
}
