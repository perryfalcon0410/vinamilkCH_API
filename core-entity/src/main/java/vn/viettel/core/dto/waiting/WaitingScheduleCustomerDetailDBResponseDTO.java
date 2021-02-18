package vn.viettel.core.dto.waiting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface WaitingScheduleCustomerDetailDBResponseDTO {
    Long getBookingId();

    Long getReceptionId();

    Long getCustomerId();

    Long getStatus();

    String getStatusName();

    Long getBookingReferrer();

    String getBookingReferrerName();

    LocalDateTime getStartScheduleDate();

    LocalDateTime getEndScheduleDate();

    LocalDate getDate();

    LocalTime getTime();

    Long getHairdresserId();

    Long getStartMilestone();

    Long getMilestoneLength();
}
