package vn.viettel.core.db.entity.status;

public enum PlanCategory {
    LIGHT, BASIC, STANDARD, ADVANCED;

    public static PlanCategory getByName(String name) {
        for (PlanCategory value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
