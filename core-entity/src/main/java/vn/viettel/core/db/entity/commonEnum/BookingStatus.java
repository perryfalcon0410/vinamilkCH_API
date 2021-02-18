package vn.viettel.core.db.entity.commonEnum;

public enum BookingStatus {
    NOT_BOOKING_YET(1, "No Booking", "No Booking"),
    UNDER_RESERVATION(2, "予約中", "Is Booked but not Checked in"),
    BOOKING_LATE_WAITING_CONFIRM(3, "Is booked, is checked in, but is waiting, and already confirmed by staff", "Is booked but is waiting, and already confirmed by staff"),
    BOOKING_ACCEPTATION(4, "受付中", "The staff accepted the booking"),
    CANCEL_BOOKING(5, "キャンセル","Cancel Booking"),
    DURING_TREATMENT(6, "施術中", "During treatment"),
    TREATMENT_COMPLETED(7, "施術完了", "Treatment completed"),
    ACCOUNTING_COMPLETED(8, "会計完了", "Accounting completed"),
    TEMPORARY_HOLD(9, "一時保留", "Temporary holding"),
    ALL(10, "すべて", "All");

    private long id;

    private String name;

    private String bookingTypeName;

    BookingStatus(long id, String name, String bookingTypeName) {
        this.id = id;
        this.name = name;
        this.bookingTypeName = bookingTypeName;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBookingTypeName() {
        return bookingTypeName;
    }

    public static BookingStatus getBookingStatusById(Long id) {
        for (BookingStatus e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
