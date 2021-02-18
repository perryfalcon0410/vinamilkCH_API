package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;
import java.util.List;

public class CouponCheckConditionDTO {
    private boolean checkCouponDelete;

    private boolean checkCouponRelease;

    private boolean checkCouponCombine;

    private boolean checkCouponValidDateRange;

    private boolean checkCouponInSalon;

    private LocalDateTime bookingDate;

    private boolean checkCouponUse;

    private String couponCode;

    private Long memberId;

    private List<Long> couponIds;

    private Long bookingId;

    private Long salonId;

    public boolean isCheckCouponDelete() {
        return checkCouponDelete;
    }

    public void setCheckCouponDelete(boolean checkCouponDelete) {
        this.checkCouponDelete = checkCouponDelete;
    }

    public boolean isCheckCouponRelease() {
        return checkCouponRelease;
    }

    public void setCheckCouponRelease(boolean checkCouponRelease) {
        this.checkCouponRelease = checkCouponRelease;
    }

    public boolean isCheckCouponCombine() {
        return checkCouponCombine;
    }

    public void setCheckCouponCombine(boolean checkCouponCombine) {
        this.checkCouponCombine = checkCouponCombine;
    }

    public boolean isCheckCouponValidDateRange() {
        return checkCouponValidDateRange;
    }

    public void setCheckCouponValidDateRange(boolean checkCouponValidDateRange) {
        this.checkCouponValidDateRange = checkCouponValidDateRange;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isCheckCouponUse() {
        return checkCouponUse;
    }

    public void setCheckCouponUse(boolean checkCouponUse) {
        this.checkCouponUse = checkCouponUse;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<Long> getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(List<Long> couponIds) {
        this.couponIds = couponIds;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public boolean isCheckCouponInSalon() {
        return checkCouponInSalon;
    }

    public void setCheckCouponInSalon(boolean checkCouponInSalon) {
        this.checkCouponInSalon = checkCouponInSalon;
    }
}
