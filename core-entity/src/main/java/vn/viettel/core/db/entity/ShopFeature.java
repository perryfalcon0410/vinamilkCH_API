package vn.viettel.core.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_features")
public class ShopFeature extends BaseEntity {

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "charged_at")
    private LocalDateTime chargedAt;

	public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDateTime getChargedAt() {
		return chargedAt;
	}

	public void setChargedAt(LocalDateTime chargedAt) {
		this.chargedAt = chargedAt;
	}
}
