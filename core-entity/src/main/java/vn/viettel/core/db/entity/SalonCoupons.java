package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_coupons")
@AttributeOverride(name = "id", column = @Column(name = "salon_coupon_id"))
public class SalonCoupons extends BaseEntity{

    @Column(name="salon_id", nullable = false)
    private Long salonId;

    @Column(name="coupon_id", nullable = false)
    private Long couponId;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}
