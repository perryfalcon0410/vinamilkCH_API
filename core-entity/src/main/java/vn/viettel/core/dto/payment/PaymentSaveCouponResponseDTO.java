package vn.viettel.core.dto.payment;

import vn.viettel.core.db.entity.ReceptionCoupon;

import java.util.List;

public class PaymentSaveCouponResponseDTO {
    private Long bookingId;
    private List<ReceptionCoupon> receptionCoupons;
    private Double couponTotal = 0.0;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<ReceptionCoupon> getReceptionCoupons() {
        return receptionCoupons;
    }

    public void setReceptionCoupons(List<ReceptionCoupon> receptionCoupons) {
        this.receptionCoupons = receptionCoupons;
    }

    public Double getCouponTotal() {
        return couponTotal;
    }

    public void setCouponTotal(Double couponTotal) {
        this.couponTotal = couponTotal;
    }
}
