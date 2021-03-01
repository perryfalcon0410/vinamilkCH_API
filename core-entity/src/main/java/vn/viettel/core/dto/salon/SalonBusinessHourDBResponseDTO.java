package vn.viettel.core.dto.salon;

import java.time.LocalTime;

public interface SalonBusinessHourDBResponseDTO {
    Long getSalonId();

    Long getBusinessHourTypeId();

    String getDateOfWeek();

    LocalTime getStartMorningHour();

    LocalTime getEndMorningHour();

    String getStartHour();

    String getEndHour();
}
