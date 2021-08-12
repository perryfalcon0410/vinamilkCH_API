package vn.viettel.core.db.entity.enums.receipt;

import java.util.Arrays;

public enum StockAdjustmentStatus {
    TRANSFER_CH(1, "DA CHUYEN CH"),
    TRANSFER_CH_YET(2, "CHUA CHUYEN CH"),
    CONFIRMED_CH(3, "DA XAC NHAN CH"),
    CANCELED(0, "DA HUY"),
            ;
    private final int type;
    private final String value;

    private StockAdjustmentStatus(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int typeCode() {return type;}
    public String typeValue() {return value;}

    public static String getValueOf(int value) {
        return Arrays.stream(values())
                .filter(cus -> cus.type == value)
                .findFirst().get().toString();
    }
}
