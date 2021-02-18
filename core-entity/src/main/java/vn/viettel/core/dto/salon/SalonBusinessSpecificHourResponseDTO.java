package vn.viettel.core.dto.salon;

import java.time.LocalDate;
import java.time.LocalTime;

public class SalonBusinessSpecificHourResponseDTO {
    private Long salonId;
    private LocalDate date;
    private LocalTime startHour;
    private LocalTime endHour;
    private Boolean checkOffDay;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Boolean getCheckOffDay() {
        return checkOffDay;
    }

    public void setCheckOffDay(Boolean checkOffDay) {
        this.checkOffDay = checkOffDay;
    }
}
