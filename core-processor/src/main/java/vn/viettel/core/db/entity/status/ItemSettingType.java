package vn.viettel.core.db.entity.status;

public enum ItemSettingType {

    TABLE(0), COUNTER(1);

    private int value;

    ItemSettingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
