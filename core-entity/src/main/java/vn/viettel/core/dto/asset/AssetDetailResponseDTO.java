package vn.viettel.core.dto.asset;

public class AssetDetailResponseDTO {
    private Long assetDetailId;
    private Long assetId;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
