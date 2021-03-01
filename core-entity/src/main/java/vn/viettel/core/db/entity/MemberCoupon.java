package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "members_coupons")
@AttributeOverride(name = "id", column = @Column(name = "member_coupon_id"))
public class MemberCoupon extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name="is_used")
    private Boolean isUsed;

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}
