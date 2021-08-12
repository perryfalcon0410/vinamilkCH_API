package vn.viettel.core.db.entity.enums.customer;

import java.util.Arrays;
import java.util.Optional;

public enum CardMemberType {
    BRONZE_CUSTOMER(0, "KHACH HANG DONG"),
    GOLD_CUSTOMER(1, "KHACH HANG VANG"),
    DIAMOND_CUSTOMER(2, "KHACH HANG KIM CUONG"),
    ;
    private final int type;
    private final String value;

    private CardMemberType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int typeCode() {return type;}
    public String cardTypeValue() {return value;}

    public static CardMemberType getValueOf(int value) {
        return Arrays.stream(values())
                .filter(card -> card.type == value)
                .findFirst().get();
    }
}
