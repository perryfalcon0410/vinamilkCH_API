package vn.viettel.core.db.entity.status;

public enum PlanType {

    NORMAL("NORMAL"), CAMPAIGN("CAMPAIGN"), SPECIFIC_CAMPAIGN("SPECIFIC_CAMPAIGN");

    private String type;

    private PlanType(String type) {
        this.type = type;
    }

    public String value() {
        return this.type;
    }

    public String stringValue() {
        return this.type;
    }

    public static PlanType getByType(String type) {
        for (PlanType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
