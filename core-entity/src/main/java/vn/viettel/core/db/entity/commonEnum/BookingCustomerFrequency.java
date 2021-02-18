package vn.viettel.core.db.entity.commonEnum;

public enum BookingCustomerFrequency {
    NEW_CUSTOMER(1, "新客", "New Customer"),
    OLD_CUSTOMER(2, "固客", "Returning Customer");

    private long id;

    private String name;

    private String type;

    BookingCustomerFrequency(long id, String name, String type) {
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
}
