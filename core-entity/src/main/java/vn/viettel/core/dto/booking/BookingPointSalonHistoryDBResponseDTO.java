package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;

public interface BookingPointSalonHistoryDBResponseDTO {
    Long getBookingId();

    Long getReceptionId();

    Long getCustomerId();

    Long getSalonId();

    LocalDateTime getStartScheduleDate();

    Double getPoint();
}
