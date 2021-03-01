package vn.viettel.core.db.entity.enums.customer;

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
}
