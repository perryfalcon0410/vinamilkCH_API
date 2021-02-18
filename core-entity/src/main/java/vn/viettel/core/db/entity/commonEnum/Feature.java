package vn.viettel.core.db.entity.commonEnum;

public enum Feature {

    NO_CHECK_FEATURE(0, "no check feature", "no check feature"),
    RESERVATION(1, "予約", "Reservation"),
    TREATMENT(2, "施術", "Treatment"),
    ACCOUNTING(3, "会計", "Accounting"),
    CUSTOMER_MANAGEMENT(4, "顧客管理", "CustomerManagement"),
    INVENTORY_CONTROL(5, "在庫管理", "InventoryControl"),
    STORE_MANAGEMENT(6, "店舗管理", "StoreManagement"),
    SALE_MANAGEMENT(7, "売上管理", "SaleManagement"),
    RECEPTION(8, "受付", "Reception"),
    STAFF_MANAGEMENT(9, "スタッフ管理", "StaffManagement"),
    REPORT_MANAGEMENT(10, "報告書管理", "ReportManagement"),
    DATA_CAPTURE(11, "データ取込", "DataCapture"),
    MASTER_ADMIN(12, "マスタ管理", "MasterAdmin"),
    ANALYSIS(13, "分析", "Analysis");

    private long id;

    private String name;

    private String description;

    Feature(long id, String name, String description) {
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

    public Feature getFeatureById() {
        for(Feature e : values()) {
            if(e.id == id) return e;
        }
        return null;
    }

    public Feature getFeatureByName(String name) {
        for (Feature e : values()) {
            if (e.name.contentEquals(name)) return e;
        }
        return null;
    }
}
