package vn.viettel.core.db.entity.commonEnum;

public enum BookingSource {
    WEB_BOOKING(1, "Web予約", "New Customer"),
    SALON_BOOKING(2, "受付予約", "Returning Customer"),
    DUAL_BOOKING(3, "電話予約", "Dual booking?");

    private long id;

    private String name;

    private String type;

    BookingSource(long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static BookingSource getBookingSourceById(Long id) {
        for(BookingSource e : values()) {
            if(e.id == id) return e;
        }
        return null;
    }
}
