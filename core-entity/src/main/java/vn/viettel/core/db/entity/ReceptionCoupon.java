package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reception_coupons")
@AttributeOverride(name = "id", column = @Column(name = "reception_coupon_id"))
public class ReceptionCoupon extends BaseEntity {
    @Column(name = "reception_id", nullable = false)
    private Long receptionId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "amount")
    private Long amount;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
