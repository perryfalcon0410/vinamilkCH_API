package vn.viettel.core.dto.waiting;

import java.time.LocalDateTime;

public interface CustomerVisitedDetailDBResponseDTO {
    Long getBookingId();

    Long getReceptionId();

    LocalDateTime getStartScheduleDate();

    LocalDateTime getEndScheduleDate();

    Double getTotal();

    String getMemo();

    Long getStatus();
}
