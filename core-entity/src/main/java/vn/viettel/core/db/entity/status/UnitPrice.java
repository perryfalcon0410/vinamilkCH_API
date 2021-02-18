package vn.viettel.core.db.entity.status;

public enum UnitPrice {
    YEN(0, "yen", "jpy");

    private int id;
    private String name;
    private String currency;

    UnitPrice(int id, String name, String currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCurrency() {
        return this.currency;
    }
}
