package vn.viettel.core.db.entity.enums.receipt;

import java.util.Arrays;

public enum StockBorrowingStatus {
    CONFIRMED(1, "DA DUYET"),
    REFUSE(2, "TU CHOI"),
    APPROVED_IMPORT(3, "XAC NHAN NHAP"),
    APPROVED_EXPORT(4, "XAC NHAN XUAT"),
    IMPORTED(5, "DA NHAP"),
    EXPIRED(6, "QUA HAN"),
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
