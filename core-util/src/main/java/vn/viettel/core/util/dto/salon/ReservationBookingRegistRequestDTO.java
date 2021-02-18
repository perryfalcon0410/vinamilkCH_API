package vn.viettel.core.util.dto.salon;

import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

public class ReservationBookingRegistRequestDTO {
    SalonConfirmationDetailDTO detail;

    BookingConditionDTO canBeBooking;

    public ReservationBookingRegistRequestDTO() {
    }

    public ReservationBookingRegistRequestDTO(SalonConfirmationDetailDTO detail, BookingConditionDTO canBeBooking) {
        this.detail = detail;
        this.canBeBooking = canBeBooking;
    }

    public SalonConfirmationDetailDTO getDetail() {
        return detail;
    }

    public void setDetail(SalonConfirmationDetailDTO detail) {
        this.detail = detail;
    }

    public BookingConditionDTO getCanBeBooking() {
        return canBeBooking;
    }

    public void setCanBeBooking(BookingConditionDTO canBeBooking) {
        this.canBeBooking = canBeBooking;
    }
}
