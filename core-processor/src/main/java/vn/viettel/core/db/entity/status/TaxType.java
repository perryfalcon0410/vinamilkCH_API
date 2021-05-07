package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaxType {
    @JsonProperty("EXCLUDE_TAX")
    ITEM_TYPE(0), // exclude tax

    @JsonProperty("INCLUDE_TAX")
    SPECIFIC_ITEM(1); // include tax

    private int id;

    TaxType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static TaxType valueOf(int id) {
        for (TaxType value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
