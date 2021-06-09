package vn.viettel.core.db.entity.status;

public enum CardType {

    VISA(0, "VISA"),
    MASTERCARD(1, "MasterCard"), 
    JCB(2, "JCB"), 
    AMERICAN_EXPRESS(3, "AMERICAN EXPRESS"), 
    DINERS_CLUB(4, "Diners Club INTERNATIONAL"), 
    DISCOVER_NETWORK(5, "DISCOVER NETWORK"), 
    PAYID(6, "PAYID");

    private int id;
    private String name;

    private CardType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
