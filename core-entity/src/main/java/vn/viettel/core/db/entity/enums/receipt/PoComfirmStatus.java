package vn.viettel.core.db.entity.enums.receipt;

import java.util.Arrays;

public enum PoComfirmStatus {
    NO_IMPORT_YET(0, "CHUA NHAP"),
    IMPORTED(1, "DA NHAP"),
    HO_IMPORT(2, "HO NHAP"),
    CANCELED(3, "DA HUY"),
    NOT_IMPORT(4, "KHONG NHAP"),
    ;
    private final int type;
    private final String value;

    private PoComfirmStatus(int type, String value) {
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
