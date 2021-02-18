package vn.viettel.core.db.entity.commonEnum;

public enum ReceptionManagementType {
    WINDING_ONE_LIQUID_APPLICATION(1, "ワインディング一液塗布", "Winding one liquid application"),
    TWO_COMPONENT_APPLICATION(2, "２液塗布", "Two-component application"),
    OUT_SINK(3, "アウトお流し", "Out sink"),
    BLOW_FINISH(4, "ブロー仕上げ", "Blow finish");

    private long id;

    private String name;

    private String description;

    ReceptionManagementType(long id, String name, String description) {
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

    public static ReceptionManagementType getReceptionManagementTypeById(Long id) {
        for (ReceptionManagementType e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}
