package vn.viettel.core.db.entity.commonEnum;

public enum MemberListColumn {
    ID_STR(1, "idStr", "The id in format of 10 characters"),
    SALON_NAME(2, "salonName", "The name of salon that user belong to or the salon user is managing"),
    MEMBER_TYPE_NAME(3, "memberTypeName", "Is Customer or Member"),
    FULL_NAME(4, "fullName", "Form from first, last, katakana names"),
    SEX(5, "genderStr", "Man or Woman");

    private long id;

    private String name;

    private String description;

    MemberListColumn(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static MemberListColumn getMemberListColumnById(Long id) {
        for (MemberListColumn e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
