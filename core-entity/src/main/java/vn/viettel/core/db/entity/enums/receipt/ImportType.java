package vn.viettel.core.db.entity.enums.receipt;

import java.util.Arrays;

public enum ImportType {
    PO_COMFIRM(0, "NHAP HANG"),
    ADJUSTMENT(1, "DIEU CHINH"),
    BORROW(2, "VAY MUON"),
    ;
    private final int type;
    private final String value;

    private ImportType(int type, String value) {
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
