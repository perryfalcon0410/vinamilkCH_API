package vn.viettel.core.dto.asset;

import java.util.List;

public class AssetGenerateNumResponseDTO {
    private String generateManaNum;
    private List<AssetResponseDTO> assetCategories;

    public String getGenerateManaNum() {
        return generateManaNum;
    }

    public void setGenerateManaNum(String generateManaNum) {
        this.generateManaNum = generateManaNum;
    }

    public List<AssetResponseDTO> getAssetCategories() {
        return assetCategories;
    }

    public void setAssetCategories(List<AssetResponseDTO> assetCategories) {
        this.assetCategories = assetCategories;
    }
}
