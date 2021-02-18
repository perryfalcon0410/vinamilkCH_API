package vn.viettel.core.db.entity.status.converter;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CustomerPaymentStatus {
    @JsonProperty("OPEN")
    OPEN(0),
    @JsonProperty("PAID")
    PAID(1),
    @JsonProperty("PARTIAL_REFUNDED")
    PARTIAL_REFUNDED(2),
    @JsonProperty("REFUNDED")
    REFUNDED(3);

    private int id;

    CustomerPaymentStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static CustomerPaymentStatus valueOf(int id) {
        for (CustomerPaymentStatus value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
