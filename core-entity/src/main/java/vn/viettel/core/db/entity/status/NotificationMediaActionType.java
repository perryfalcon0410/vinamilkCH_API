package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationMediaActionType {

    @JsonProperty("NOTHING")
    NOTHING(0),
    @JsonProperty("LINK")
    LINK(1),
    @JsonProperty("COUPON")
    COUPON(2),
    @JsonProperty("APPLICATION_CAMPAIGN")
    APPLICATION_CAMPAIGN(3);

    private int id;

    NotificationMediaActionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static NotificationMediaActionType valueOf(Integer id) {
        if (id != null) {
            for (NotificationMediaActionType type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
