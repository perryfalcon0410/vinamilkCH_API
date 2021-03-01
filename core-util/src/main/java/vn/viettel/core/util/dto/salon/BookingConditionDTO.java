package vn.viettel.core.util.dto.salon;

import vn.viettel.core.dto.booking.TimeSheetScheduleDTO;
import vn.viettel.core.ResponseMessage;

import java.time.LocalDateTime;

public class BookingConditionDTO {
    private ResponseMessage msg;

    private LocalDateTime currentBookingDateTime;

    private TimeSheetScheduleDTO dateOldSchedule;

    private Integer totalTime;

    private Boolean canBeBooking = null;

    private Integer rangeTime;

    public ResponseMessage getMsg() {
        return msg;
    }

    public void setMsg(ResponseMessage msg) {
        this.msg = msg;
    }

    public LocalDateTime getCurrentBookingDateTime() {
        return currentBookingDateTime;
    }

    public void setCurrentBookingDateTime(LocalDateTime currentBookingDateTime) {
        this.currentBookingDateTime = currentBookingDateTime;
    }

    public TimeSheetScheduleDTO getDateOldSchedule() {
        return dateOldSchedule;
    }

    public void setDateOldSchedule(TimeSheetScheduleDTO dateOldSchedule) {
        this.dateOldSchedule = dateOldSchedule;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Boolean getCanBeBooking() {
        return canBeBooking;
    }

    public void setCanBeBooking(Boolean canBeBooking) {
        this.canBeBooking = canBeBooking;
    }

    public Integer getRangeTime() {
        return rangeTime;
    }

    public void setRangeTime(Integer rangeTime) {
        this.rangeTime = rangeTime;
    }
}
