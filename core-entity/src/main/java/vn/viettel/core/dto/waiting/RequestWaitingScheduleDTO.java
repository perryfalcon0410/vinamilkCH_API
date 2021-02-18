package vn.viettel.core.dto.waiting;

import java.time.LocalDate;

public class RequestWaitingScheduleDTO {
    private Long salonId;

    private LocalDate date;

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
}
