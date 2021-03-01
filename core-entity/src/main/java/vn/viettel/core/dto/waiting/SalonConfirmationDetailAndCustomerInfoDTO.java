package vn.viettel.core.dto.waiting;

import vn.viettel.core.db.entity.Booking;
import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

public class SalonConfirmationDetailAndCustomerInfoDTO {
    private SalonConfirmationDetailDTO detail;

    private Booking booking;

    private Integer errId;

    public SalonConfirmationDetailDTO getDetail() {
        return detail;
    }

    public void setDetail(SalonConfirmationDetailDTO detail) {
        this.detail = detail;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Integer getErrId() {
        return errId;
    }

    public void setErrId(Integer errId) {
        this.errId = errId;
    }
}
