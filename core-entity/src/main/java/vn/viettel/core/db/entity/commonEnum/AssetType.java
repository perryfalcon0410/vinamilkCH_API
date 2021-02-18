package vn.viettel.core.db.entity.commonEnum;

public enum AssetType {
    TEXT(1L,"text", "テキスト"),
    FILE(2L, "file", "ファイル"),
    DATE(3L, "date", "日付");

    private Long assetTypeId;
    private String typeName;
    private String description;


    AssetType() {
    }

    AssetType(Long assetTypeId, String typeName, String description) {
        this.assetTypeId = assetTypeId;
        this.typeName = typeName;
        this.description = description;
    }

    public Long getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Long assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getNameById(Long id) {
        for (AssetType e : AssetType.values()) {
            if (id == e.getAssetTypeId()) {
                return e.getTypeName();
            }
        }
        return null;
    }
}
