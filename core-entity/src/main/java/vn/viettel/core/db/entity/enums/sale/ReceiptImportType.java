package vn.viettel.core.db.entity.enums.sale;

public enum ReceiptImportType {
    POCONFIRM(0, "POConfirm"),
    POADJUSTED(1, "POAdjusted"),
    POBORROW(2, "POBorrow"),
    ;
    private final int code;
    private final String type;

    private ReceiptImportType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {return code;}
    public String getType() {return type;}
}
