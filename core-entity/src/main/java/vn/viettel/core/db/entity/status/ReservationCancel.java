package vn.viettel.core.db.entity.status;

public enum ReservationCancel {

    NOT_CANCELED(false), CANCELED(true);

    private boolean status;

    private ReservationCancel(boolean status) {
        this.status = status;
    }

    public boolean value() {
        return this.status;
    }
}
