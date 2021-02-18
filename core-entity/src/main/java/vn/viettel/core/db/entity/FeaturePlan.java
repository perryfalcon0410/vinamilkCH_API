package vn.viettel.core.db.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "features_plans")
public class FeaturePlan extends BaseEntity {

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "plan_payjp_id", length = 64)
    private String planPayjpId;

    @Column(name = "descriptions", length = 64)
    private String descriptions;

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "feature_id", nullable = false)
    private Long featureId;

//    @ManyToOne
//    @JoinColumn(name = "feature_id")
//    private Feature feature;

    public FeaturePlan() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanPayjpId() {
        return planPayjpId;
    }

    public void setPlanPayjpId(String planPayjpId) {
        this.planPayjpId = planPayjpId;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

//    public Feature getFeature() {
//        return feature;
//    }
//
//    public void setFeature(Feature feature) {
//        this.feature = feature;
//    }

}
