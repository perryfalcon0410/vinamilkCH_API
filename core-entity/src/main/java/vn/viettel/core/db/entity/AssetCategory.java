package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "asset_category")
@AttributeOverride(name = "id", column = @Column(name = "asset_category_id"))
public class AssetCategory extends BaseEntity{

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "column_type")
    private Long columnType;

    @Column(name = "hide")
    private Boolean hide;

    @Column(name = "position")
    private Long position;

    @Column(name = "company_id")
    private Long companyId;

    public AssetCategory() {
    }

    public AssetCategory(String columnName, Long columnType, Boolean hide, Long position, Long companyId) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.hide = hide;
        this.position = position;
        this.companyId = companyId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getColumnType() {
        return columnType;
    }

    public void setColumnType(Long columnType) {
        this.columnType = columnType;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
