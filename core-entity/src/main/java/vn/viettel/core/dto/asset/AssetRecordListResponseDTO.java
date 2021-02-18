package vn.viettel.core.dto.asset;

import java.util.List;

public class AssetRecordListResponseDTO {
    private Long assetId;
    private List<AssetRecordResponseDTO> record;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public List<AssetRecordResponseDTO> getRecord() {
        return record;
    }

    public void setRecord(List<AssetRecordResponseDTO> record) {
        this.record = record;
    }
}
