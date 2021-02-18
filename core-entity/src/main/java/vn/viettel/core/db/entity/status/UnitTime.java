package vn.viettel.core.db.entity.status;

public enum UnitTime {
    MINUTE(0), HOUR(1), DAY(2), WEEK(3), MONTH(4);

    private int unit;

    UnitTime(int unit) {
        this.unit = unit;
    }

    public int value() {
        return this.unit;
    }

    public int intValue() {
        return this.unit;
    }

    public Long longValue() {
        return Long.valueOf(this.unit);
    }

    public String stringValue() {
        return String.valueOf(this.unit);
    }

}
