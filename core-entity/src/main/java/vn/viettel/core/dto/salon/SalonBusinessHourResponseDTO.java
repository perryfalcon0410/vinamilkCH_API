package vn.viettel.core.dto.salon;

import java.time.LocalTime;

public class SalonBusinessHourResponseDTO {
    private Long salonId;
    private Long businessHourTypeId;
    private String dateOfWeek;
    private LocalTime startMorningHour;
    private LocalTime endMorningHour;
    private LocalTime startAfternoonHour;
    private LocalTime endAfternoonHour;
    private Boolean isValid;
    private String startHour;
    private String endHour;
    private Boolean checkOffDay;

    public SalonBusinessHourResponseDTO() {
    }

    public SalonBusinessHourResponseDTO(Long salonId, Long businessHourTypeId, String dateOfWeek, LocalTime startMorningHour, LocalTime endMorningHour, LocalTime startAfternoonHour, LocalTime endAfternoonHour, Boolean isValid, String startHour, String endHour, Boolean checkOffDay) {
        this.salonId = salonId;
        this.businessHourTypeId = businessHourTypeId;
        this.dateOfWeek = dateOfWeek;
        this.startMorningHour = startMorningHour;
        this.endMorningHour = endMorningHour;
        this.startAfternoonHour = startAfternoonHour;
        this.endAfternoonHour = endAfternoonHour;
        this.isValid = isValid;
        this.startHour = startHour;
        this.endHour = endHour;
        this.checkOffDay = checkOffDay;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getBusinessHourTypeId() {
        return businessHourTypeId;
    }

    public void setBusinessHourTypeId(Long businessHourTypeId) {
        this.businessHourTypeId = businessHourTypeId;
    }

    public String getDateOfWeek() {
        return dateOfWeek;
    }

    public void setDateOfWeek(String dateOfWeek) {
        this.dateOfWeek = dateOfWeek;
    }

    public LocalTime getStartMorningHour() {
        return startMorningHour;
    }

    public void setStartMorningHour(LocalTime startMorningHour) {
        this.startMorningHour = startMorningHour;
    }

    public LocalTime getEndMorningHour() {
        return endMorningHour;
    }

    public void setEndMorningHour(LocalTime endMorningHour) {
        this.endMorningHour = endMorningHour;
    }

    public LocalTime getStartAfternoonHour() {
        return startAfternoonHour;
    }

    public void setStartAfternoonHour(LocalTime startAfternoonHour) {
        this.startAfternoonHour = startAfternoonHour;
    }

    public LocalTime getEndAfternoonHour() {
        return endAfternoonHour;
    }

    public void setEndAfternoonHour(LocalTime endAfternoonHour) {
        this.endAfternoonHour = endAfternoonHour;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public Boolean getCheckOffDay() {
        return checkOffDay;
    }

    public void setCheckOffDay(Boolean checkOffDay) {
        this.checkOffDay = checkOffDay;
    }
}
