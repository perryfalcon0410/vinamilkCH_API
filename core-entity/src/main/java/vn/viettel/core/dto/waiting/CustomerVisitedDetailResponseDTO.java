package vn.viettel.core.dto.waiting;

import java.time.LocalDateTime;

public class CustomerVisitedDetailResponseDTO {
    private Long bookingId;

    private Long receptionId;

    private LocalDateTime startScheduleDate;

    private LocalDateTime endScheduleDate;

    private Double total;

    private String memo;

    private Long status;

    public CustomerVisitedDetailResponseDTO() {
    }

    public CustomerVisitedDetailResponseDTO(Long bookingId, Long receptionId, LocalDateTime startScheduleDate, LocalDateTime endScheduleDate, Double total, String memo, Long status) {
        this.bookingId = bookingId;
        this.receptionId = receptionId;
        this.startScheduleDate = startScheduleDate;
        this.endScheduleDate = endScheduleDate;
        this.total = total;
        this.memo = memo;
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public LocalDateTime getStartScheduleDate() {
        return startScheduleDate;
    }

    public void setStartScheduleDate(LocalDateTime startScheduleDate) {
        this.startScheduleDate = startScheduleDate;
    }

    public LocalDateTime getEndScheduleDate() {
        return endScheduleDate;
    }

    public void setEndScheduleDate(LocalDateTime endScheduleDate) {
        this.endScheduleDate = endScheduleDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
