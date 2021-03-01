package vn.viettel.core.dto.asset;

public class AssetCategoryResponseDTO {
    private Long assetCategoryId;
    private String categoryName;
    private Long type;
    private Long numSet;
    private Boolean hide;
    private Long position;

    public Long getAssetCategoryId() {
        return assetCategoryId;
    }

    public void setAssetCategoryId(Long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getNumSet() {
        return numSet;
    }

    public void setNumSet(Long numSet) {
        this.numSet = numSet;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
