package vn.viettel.core.db.entity.status;

public enum ShopTypeStatus {

    ACTIVE(1), UNACTIVE(0);

    private int status;

    private ShopTypeStatus(int status) {
        this.status = status;
    }

    public int value() {
        return this.status;
    }

    public int intValue() {
        return this.status;
    }

    public Long longValue() {
        return Long.valueOf(this.status);
    }

    public String stringValue() {
        return String.valueOf(this.status);
    }
}
