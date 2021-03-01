package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;
import java.util.List;

public class BookingHistoryListResponseDTO {
    private Long bookingId;
    private LocalDateTime finishScheduleDate;
    private Long salonId;
    private String salonName;
    private String salonMenuId;
    private List<Long> salonMenuIds;
    private String salonMenuName;
    private Long managementUserId;
    private String managementUserName;
    private Double total;
    private String qrCode;

    public BookingHistoryListResponseDTO() {
    }

    public BookingHistoryListResponseDTO(Long bookingId, LocalDateTime finishScheduleDate, Long salonId, String salonName, String salonMenuId, List<Long> salonMenuIds, String salonMenuName, Long managementUserId, String managementUserName, Double total, String qrCode) {
        this.bookingId = bookingId;
        this.finishScheduleDate = finishScheduleDate;
        this.salonId = salonId;
        this.salonName = salonName;
        this.salonMenuId = salonMenuId;
        this.salonMenuIds = salonMenuIds;
        this.salonMenuName = salonMenuName;
        this.managementUserId = managementUserId;
        this.managementUserName = managementUserName;
        this.total = total;
        this.qrCode = qrCode;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getFinishScheduleDate() {
        return finishScheduleDate;
    }

    public void setFinishScheduleDate(LocalDateTime finishScheduleDate) {
        this.finishScheduleDate = finishScheduleDate;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonMenuId() {
        return salonMenuId;
    }

    public void setSalonMenuId(String salonMenuId) {
        this.salonMenuId = salonMenuId;
    }

    public List<Long> getSalonMenuIds() {
        return salonMenuIds;
    }

    public void setSalonMenuIds(List<Long> salonMenuIds) {
        this.salonMenuIds = salonMenuIds;
    }

    public String getSalonMenuName() {
        return salonMenuName;
    }

    public void setSalonMenuName(String salonMenuName) {
        this.salonMenuName = salonMenuName;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public String getManagementUserName() {
        return managementUserName;
    }

    public void setManagementUserName(String managementUserName) {
        this.managementUserName = managementUserName;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
