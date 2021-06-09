package vn.viettel.core.db.entity.status;

public enum PaymentType {
    GROUP(0), SHOP(1), GROUP_INIT_FEE(2), SHOP_INIT_FEE(3);

    private int value;

    PaymentType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
