package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MenuType {
    @JsonProperty("SEARCH_CONTROL")
    SEARCH_CONTROL(0), // do not allow selection
    @JsonProperty("ITEM_TYPE")
    ITEM_TYPE(1), // allowed item type selection
    @JsonProperty("SPECIFIC_ITEM")
    SPECIFIC_ITEM(2); // allowed item selection

    private int id;

    MenuType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static MenuType valueOf(int id) {
        for (MenuType value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
