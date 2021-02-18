package vn.viettel.core.dto.booking;

import java.time.LocalDate;
import java.util.List;

public class CalendarScheduleResponseDTO {
    List<TimeSheetScheduleDTO> dateList;

    List<String> timeSheetKey;

    List<LocalDate> holiday;

    List<LocalDate> regularOffday;

    TimeSheetScheduleDTO currentBookingOfUser;

    public CalendarScheduleResponseDTO() {
    }

    public CalendarScheduleResponseDTO(List<TimeSheetScheduleDTO> dateList, List<String> timeSheetKey, List<LocalDate> holiday, List<LocalDate> regularOffday, TimeSheetScheduleDTO currentBookingOfUser) {
        this.dateList = dateList;
        this.timeSheetKey = timeSheetKey;
        this.holiday = holiday;
        this.regularOffday = regularOffday;
        this.currentBookingOfUser = currentBookingOfUser;
    }

    public List<TimeSheetScheduleDTO> getDateList() {
        return dateList;
    }

    public void setDateList(List<TimeSheetScheduleDTO> dateList) {
        this.dateList = dateList;
    }

    public List<String> getTimeSheetKey() {
        return timeSheetKey;
    }

    public void setTimeSheetKey(List<String> timeSheetKey) {
        this.timeSheetKey = timeSheetKey;
    }

    public List<LocalDate> getHoliday() {
        return holiday;
    }

    public void setHoliday(List<LocalDate> holiday) {
        this.holiday = holiday;
    }

    public List<LocalDate> getRegularOffday() {
        return regularOffday;
    }

    public void setRegularOffday(List<LocalDate> regularOffday) {
        this.regularOffday = regularOffday;
    }

    public TimeSheetScheduleDTO getCurrentBookingOfUser() {
        return currentBookingOfUser;
    }

    public void setCurrentBookingOfUser(TimeSheetScheduleDTO currentBookingOfUser) {
        this.currentBookingOfUser = currentBookingOfUser;
    }
}
