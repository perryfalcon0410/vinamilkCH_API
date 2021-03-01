package vn.viettel.core.dto.salon;

import vn.viettel.core.db.entity.commonEnum.DateOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;

public class HairdresserDateRoutineDTO {
    private DateOfWeek commonDate;

    private LocalDate specificDate;

    private LocalTime startHour;

    private LocalTime endHour;

    private Boolean offDayLikeSalon;

    public HairdresserDateRoutineDTO() {
    }

    public HairdresserDateRoutineDTO(DateOfWeek commonDate, LocalDate specificDate, LocalTime startHour, LocalTime endHour, Boolean offDayLikeSalon) {
        this.commonDate = commonDate;
        this.specificDate = specificDate;
        this.startHour = startHour;
        this.endHour = endHour;
        this.offDayLikeSalon = offDayLikeSalon;
    }

    public Boolean getOffDayLikeSalon() {
        return offDayLikeSalon;
    }

    public void setOffDayLikeSalon(Boolean offDayLikeSalon) {
        this.offDayLikeSalon = offDayLikeSalon;
    }

    public DateOfWeek getCommonDate() {
        return commonDate;
    }

    public void setCommonDate(DateOfWeek commonDate) {
        this.commonDate = commonDate;
    }

    public LocalDate getSpecificDate() {
        return specificDate;
    }

    public void setSpecificDate(LocalDate specificDate) {
        this.specificDate = specificDate;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }
}
