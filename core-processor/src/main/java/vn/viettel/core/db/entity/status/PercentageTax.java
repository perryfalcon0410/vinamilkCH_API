package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PercentageTax {
    @JsonProperty("TAX_10")
    ITEM_TYPE(0), // 10%

    @JsonProperty("TAX_8")
    SPECIFIC_ITEM(1); // 8%

    private int id;

    PercentageTax(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static PercentageTax valueOf(int id) {
        for (PercentageTax value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
