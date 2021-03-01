package vn.viettel.core.dto.waiting;

import java.time.LocalDate;
import java.util.List;

public class WaitingScheduleResponseDTO {

    private LocalDate date;

    private List<WaitingScheduleRulerDetailDTO> ruler;

    private List<WaitingScheduleCalendarDetailDTO> calendars;

    public WaitingScheduleResponseDTO() {
    }

    public WaitingScheduleResponseDTO(LocalDate date, List<WaitingScheduleRulerDetailDTO> ruler, List<WaitingScheduleCalendarDetailDTO> calendars) {
        this.date = date;
        this.ruler = ruler;
        this.calendars = calendars;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<WaitingScheduleRulerDetailDTO> getRuler() {
        return ruler;
    }

    public void setRuler(List<WaitingScheduleRulerDetailDTO> ruler) {
        this.ruler = ruler;
    }

    public List<WaitingScheduleCalendarDetailDTO> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<WaitingScheduleCalendarDetailDTO> calendars) {
        this.calendars = calendars;
    }
}
