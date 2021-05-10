package vn.viettel.core.db.entity.status;

public enum ReservationApprovalStatus {
    NEW(0),
    APPROVED(1),
    REJECTED(2);

    private int id;

    ReservationApprovalStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ReservationApprovalStatus valueOf(int id) {
        for (ReservationApprovalStatus status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }

    public static ReservationApprovalStatus getByName(String name) {
        for (ReservationApprovalStatus status : values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return null;
    }

}
