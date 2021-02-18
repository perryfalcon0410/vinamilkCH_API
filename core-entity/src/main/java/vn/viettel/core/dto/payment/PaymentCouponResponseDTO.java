package vn.viettel.core.dto.payment;

import vn.viettel.core.dto.salon.CouponDetailDTO;
import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

import java.util.List;

public class PaymentCouponResponseDTO {
    private SalonConfirmationDetailDTO salonConfirmationDetailDTO;

    private List<CouponDetailDTO> coupons;

    public SalonConfirmationDetailDTO getSalonConfirmationDetailDTO() {
        return salonConfirmationDetailDTO;
    }

    public void setSalonConfirmationDetailDTO(SalonConfirmationDetailDTO salonConfirmationDetailDTO) {
        this.salonConfirmationDetailDTO = salonConfirmationDetailDTO;
    }

    public List<CouponDetailDTO> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponDetailDTO> coupons) {
        this.coupons = coupons;
    }
}
