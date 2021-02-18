package vn.viettel.core.dto.waiting;

import java.time.LocalDateTime;

public interface SalonBookingWaitingDetailDBResponseDTO {
    Long getReceptionId();

    Long getBookingId();

    String getBookingCode();

    Long getCustomerId();

    Long getStatus();

    Long getBookingReferrer();

    LocalDateTime getStartScheduleDate();

    LocalDateTime getEndScheduleDate();

    Long getSalonId();
}
