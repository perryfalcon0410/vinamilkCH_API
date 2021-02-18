package vn.viettel.core.db.entity.commonEnum;

public enum SalonStyleType {
    SHORT(1, "ショート", "Short"),
    MEDIUM(2, "ミディアム", "Medium"),
    LONG(3, "ロング", "Long"),
    OTHER(4, "その他", "Other");

    private long id;

    private String name;

    private String description;

    SalonStyleType(long id, String name, String description) {
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

    public static SalonStyleType getSalonStyleTypeById(Long id) {
        for (SalonStyleType e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
