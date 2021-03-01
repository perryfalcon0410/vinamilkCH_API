package vn.viettel.core.dto.reservation;

import vn.viettel.core.dto.salon.CouponCheckConditionDTO;
import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

public class SalonConfirmationWithCouponConditionDTO {
    private SalonConfirmationDetailDTO detail;

    private CouponCheckConditionDTO couponCondition;

    public SalonConfirmationWithCouponConditionDTO() {
    }

    public SalonConfirmationWithCouponConditionDTO(SalonConfirmationDetailDTO detail, CouponCheckConditionDTO couponCondition) {
        this.detail = detail;
        this.couponCondition = couponCondition;
    }

    public SalonConfirmationDetailDTO getDetail() {
        return detail;
    }

    public void setDetail(SalonConfirmationDetailDTO detail) {
        this.detail = detail;
    }

    public CouponCheckConditionDTO getCouponCondition() {
        return couponCondition;
    }

    public void setCouponCondition(CouponCheckConditionDTO couponCondition) {
        this.couponCondition = couponCondition;
    }
}
