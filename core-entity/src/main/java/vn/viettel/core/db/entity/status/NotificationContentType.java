package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationContentType {
    @JsonProperty("TEXT_ONLY")
    TEXT_ONLY(0),
    @JsonProperty("TEXT_AND_PICTURE_TOP")
    TEXT_AND_PICTURE_TOP(1),
    @JsonProperty("TEXT_AND_PICTURE_BOTTOM")
    TEXT_AND_PICTURE_BOTTOM(2),
    @JsonProperty("TEXT_AND_MOVIE_TOP")
    TEXT_AND_MOVIE_TOP(3),
    @JsonProperty("TEXT_AND_MOVIE_BOTTOM")
    TEXT_AND_MOVIE_BOTTOM(4);

    private int id;

    NotificationContentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static NotificationContentType valueOf(Integer id) {
        if (id != null) {
            for (NotificationContentType type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }

}
