package vn.viettel.core.dto.reception;

public class CheckCouponReleasedResponseDTO {
    private Long couponId;
    private Boolean checkCouponReleased;

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Boolean getCheckCouponReleased() {
        return checkCouponReleased;
    }

    public void setCheckCouponReleased(Boolean checkCouponReleased) {
        this.checkCouponReleased = checkCouponReleased;
    }
}
