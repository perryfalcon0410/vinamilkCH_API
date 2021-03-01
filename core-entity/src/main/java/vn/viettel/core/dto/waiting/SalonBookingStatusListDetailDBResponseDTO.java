package vn.viettel.core.dto.waiting;

import java.time.LocalDateTime;

public interface SalonBookingStatusListDetailDBResponseDTO {
    String getBookingCode();

    Long getBookingId();

    Long getReceptionId();

    Long getCustomerId();

    Long getHairdresserId();

    LocalDateTime getStartScheduleDate();

    LocalDateTime getEndScheduleDate();

    Long getStatusId();

    String getStatusName();
}
