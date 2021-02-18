package vn.viettel.core.db.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CompanyProductLayoutType {
    @JsonProperty("TEXT_ONLY")
    TEXT_ONLY(0),
    @JsonProperty("PICTURE_AND_TEXT_TOP")
    TEXT_AND_PICTURE_TOP(1),
        @JsonProperty("PICTURE_TOP_TEXT_BOTTOM")
    PICTURE_TOP_TEXT_BOTTOM(2);
    private int id;

    CompanyProductLayoutType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static CompanyProductLayoutType valueOf(Integer id) {
        if (id != null) {
            for (CompanyProductLayoutType type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }

}
