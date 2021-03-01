package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "asset_details")
@AttributeOverride(name = "id", column = @Column(name = "asset_detail_id"))
public class AssetDetails extends BaseEntity{

    @Column(name = "asset_category_id")
    private Long assetCategoryId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "data")
    private String data;

    public AssetDetails() {
    }

    public AssetDetails(Long assetCategoryId, Long assetId, String data) {
        this.assetCategoryId = assetCategoryId;
        this.assetId = assetId;
        this.data = data;
    }

    public Long getAssetCategoryId() {
        return assetCategoryId;
    }

    public void setAssetCategoryId(Long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
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
