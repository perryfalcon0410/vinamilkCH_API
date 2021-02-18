package vn.viettel.core.db.entity.status;

public enum CustomerPaymentStatus {
    OPEN(0),
    PAID(1),
    REFUNDED_PARTIAL(2),
    REFUNDED(3);
    private int value;

    CustomerPaymentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static CustomerPaymentStatus valueOf(int id) {
        for (CustomerPaymentStatus status : values()) {
            if (status.getValue() == id) {
                return status;
            }
        }
        return null;
    }

    public static CustomerPaymentStatus getByName(String name) {
        for (CustomerPaymentStatus payment : values()) {
            if (payment.name().equals(name)) {
                return payment;
            }
        }
        return null;
    }
}
