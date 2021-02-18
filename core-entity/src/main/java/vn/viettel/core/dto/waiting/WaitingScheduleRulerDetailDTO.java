package vn.viettel.core.dto.waiting;

import java.time.LocalTime;
import java.util.List;

public class WaitingScheduleRulerDetailDTO {
    private LocalTime time;

    private List<Long> numberOfReservation;

    private List<Long> totalOfReservation;

    public WaitingScheduleRulerDetailDTO() {
    }

    public WaitingScheduleRulerDetailDTO(LocalTime time, List<Long> numberOfReservation, List<Long> totalOfReservation) {
        this.time = time;
        this.numberOfReservation = numberOfReservation;
        this.totalOfReservation = totalOfReservation;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public List<Long> getNumberOfReservation() {
        return numberOfReservation;
    }

    public void setNumberOfReservation(List<Long> numberOfReservation) {
        this.numberOfReservation = numberOfReservation;
    }

    public List<Long> getTotalOfReservation() {
        return totalOfReservation;
    }

    public void setTotalOfReservation(List<Long> totalOfReservation) {
        this.totalOfReservation = totalOfReservation;
    }
}
