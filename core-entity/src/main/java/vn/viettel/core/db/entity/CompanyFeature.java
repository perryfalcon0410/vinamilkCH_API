package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "company_features")
@AttributeOverride(name = "id", column = @Column(name = "company_feature_id"))
public class CompanyFeature extends BaseEntity {

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "feature_id")
    private Long featureId;

    @Column(name = "status")
    private Boolean status;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
