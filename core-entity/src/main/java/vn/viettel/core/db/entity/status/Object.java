package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Object {

    @JsonProperty("GROUP")
    GROUP(0),
    @JsonProperty("SHOP")
    SHOP(1),
    @JsonProperty("COMPANY")
    COMPANY(2);

    private int id;

    Object(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Object valueOf(int id) {
        for (Object object : values()) {
            if (object.getId() == id) {
                return object;
            }
        }
        return null;
    }

    public static Object getByName(String name) {
        for (Object object : values()) {
            if (object.name().equals(name)) {
                return object;
            }
        }
        return null;
    }

}
