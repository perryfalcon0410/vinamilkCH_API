package vn.viettel.core.dto.asset;

public class AssetResponseDTO {
    private Long assetDetailId;
    private Long assetId;
    private Long assetCategoryId;
    private String assetCategoryName;
    private String data;
    private String type;

    public Long getAssetDetailId() {
        return assetDetailId;
    }

    public void setAssetDetailId(Long assetDetailId) {
        this.assetDetailId = assetDetailId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getAssetCategoryId() {
        return assetCategoryId;
    }

    public void setAssetCategoryId(Long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }

    public String getAssetCategoryName() {
        return assetCategoryName;
    }

    public void setAssetCategoryName(String assetCategoryName) {
        this.assetCategoryName = assetCategoryName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
