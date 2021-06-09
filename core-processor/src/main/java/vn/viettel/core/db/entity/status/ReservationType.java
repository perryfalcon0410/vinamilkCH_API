package vn.viettel.core.db.entity.status;

public enum ReservationType {
    INSTANT(0),
    REQUEST(1);

    private int id;

    ReservationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ReservationType valueOf(int id) {
        for (ReservationType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public static ReservationType getByName(String name) {
        for (ReservationType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

}
