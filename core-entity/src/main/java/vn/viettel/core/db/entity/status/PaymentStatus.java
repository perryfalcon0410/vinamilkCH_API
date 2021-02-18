package vn.viettel.core.db.entity.status;

public enum PaymentStatus {

    PAID(0), REFUND(1), ERROR(2);

    private int value;

    PaymentStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
