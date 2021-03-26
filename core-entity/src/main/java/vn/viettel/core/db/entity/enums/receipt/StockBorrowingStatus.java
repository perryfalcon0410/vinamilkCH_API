package vn.viettel.core.db.entity.enums.receipt;

import java.util.Arrays;

public enum StockBorrowingStatus {
    CONFIRMED(1, "DA DUYET"),
    REFUSE(2, "TU CHOI"),
    APPLY_IMPORT(3, "XAC NHAN NHAP"),
    APPLY_EXPORT(4, "XAC NHAN XUAT"),
    EXPIRED(4, "QUA HAN"),
    ;
    private final int type;
    private final String value;

    private StockBorrowingStatus(int type, String value) {
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
