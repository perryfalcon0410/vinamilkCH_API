package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ObjectShop {

    @JsonProperty("FULLY")
    FULLY(0),
    @JsonProperty("SIMPLE")
    SIMPLE(1);

    private int id;

    ObjectShop(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ObjectShop valueOf(int id) {
        for (ObjectShop objectShop : values()) {
            if (objectShop.getId() == id) {
                return objectShop;
            }
        }
        return null;
    }
}
