package vn.viettel.core.dto.asset;

import java.util.List;

public class AssetCategoryListResponseDTO {
    private List<AssetCategoryResponseDTO> assetCategories;
    private List<AssetTypeResponseDTO> assetTypes;

    public List<AssetCategoryResponseDTO> getAssetCategories() {
        return assetCategories;
    }

    public void setAssetCategories(List<AssetCategoryResponseDTO> assetCategories) {
        this.assetCategories = assetCategories;
    }

    public List<AssetTypeResponseDTO> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AssetTypeResponseDTO> assetTypes) {
        this.assetTypes = assetTypes;
    }
}
