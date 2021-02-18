package vn.viettel.core.db.entity.commonEnum;

public enum SalonPointType {
    EVERY_VISIT(1, "来店ごとの付与ポイント", "How many points grant per each visit "),
    POINT_PER_TREATMENT(2, "施術内容ごとの付与ポイント", "How many points grant per each treatment"),
    POINT_PER_TOTAL(3, "合計金額で付与ポイント", "How many point grant per total cost (with tax)"),
    ;

    private long id;

    private String name;

    private String description;

    SalonPointType(long id, String name, String description) {
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

    public static SalonPointType getSalonPointTypeById(Long id) {
        for (SalonPointType e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
