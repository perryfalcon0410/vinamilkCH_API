package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MenuPriceCalculatedMethod {

    @JsonProperty("CALCULATED_BY_PEOPLE")
    CALCULATED_BY_PEOPLE(0),
    @JsonProperty("CALCULATED_BY_MENU")
    CALCULATED_BY_MENU(1);

    private int id;

    MenuPriceCalculatedMethod(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static MenuPriceCalculatedMethod valueOf(Integer id) {
        if (id != null) {
            for (MenuPriceCalculatedMethod method : values()) {
                if (id.equals(method.getId())) {
                    return method;
                }
            }
        }
        return null;
    }
}
