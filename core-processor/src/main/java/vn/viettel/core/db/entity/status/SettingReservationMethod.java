package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SettingReservationMethod {

    @JsonProperty("WEB")
    WEB(0),
    @JsonProperty("WEB_AND_PHONE")
    WEB_AND_PHONE(1);

    private int id;

    SettingReservationMethod(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static SettingReservationMethod valueOf(int id) {
        for (SettingReservationMethod value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
