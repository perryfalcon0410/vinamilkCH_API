package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;

public class BookingPointSalonHistoryResponseDTO {
    private Long bookingId;

    private Long receptionId;

    private Long customerId;

    private Long salonId;

    private LocalDateTime startScheduleDate;

    private Double point;

    private String salonName;

    private String salonSlug;

    private Long companyId;

    private String companySlug;

    public BookingPointSalonHistoryResponseDTO() {
    }

    public BookingPointSalonHistoryResponseDTO(Long bookingId, Long receptionId, Long customerId, Long salonId, LocalDateTime startScheduleDate, Double point, String salonName, String salonSlug, Long companyId, String companySlug) {
        this.bookingId = bookingId;
        this.receptionId = receptionId;
        this.customerId = customerId;
        this.salonId = salonId;
        this.startScheduleDate = startScheduleDate;
        this.point = point;
        this.salonName = salonName;
        this.salonSlug = salonSlug;
        this.companyId = companyId;
        this.companySlug = companySlug;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public LocalDateTime getStartScheduleDate() {
        return startScheduleDate;
    }

    public void setStartScheduleDate(LocalDateTime startScheduleDate) {
        this.startScheduleDate = startScheduleDate;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanySlug() {
        return companySlug;
    }

    public void setCompanySlug(String companySlug) {
        this.companySlug = companySlug;
    }
}
