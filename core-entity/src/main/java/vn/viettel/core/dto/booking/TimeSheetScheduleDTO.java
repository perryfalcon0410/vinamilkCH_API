package vn.viettel.core.dto.booking;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSheetScheduleDTO {
    LocalDate date;

    List<Map<String, Boolean>> timeSheet;

    public TimeSheetScheduleDTO() {
    }

    public TimeSheetScheduleDTO(LocalDate date, List<Map<String, Boolean>> timeSheet) {
        this.date = date;
        this.timeSheet = timeSheet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Map<String, Boolean>> getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(List<Map<String, Boolean>> timeSheet) {
        this.timeSheet = timeSheet;
    }
}
