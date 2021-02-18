package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;
import java.util.List;

public class BookingHistoryDetailResponseDTO {
    private Long bookingId;
    private Long salonId;
    private String salonName;
    private String salonMenuId;
    private List<Long> salonMenuIds;
    private String salonMenuName;
    private Long managementUserId;
    private String managementUserName;
    private String photoUrl;
    private List<String> photoUrls;
    private LocalDateTime finishScheduleDate;
    private Double total;

    public BookingHistoryDetailResponseDTO() {
    }

    public BookingHistoryDetailResponseDTO(Long bookingId, Long salonId, String salonName, String salonMenuId, List<Long> salonMenuIds, String salonMenuName, Long managementUserId, String managementUserName, String photoUrl, List<String> photoUrls, LocalDateTime finishScheduleDate, Double total) {
        this.bookingId = bookingId;
        this.salonId = salonId;
        this.salonName = salonName;
        this.salonMenuId = salonMenuId;
        this.salonMenuIds = salonMenuIds;
        this.salonMenuName = salonMenuName;
        this.managementUserId = managementUserId;
        this.managementUserName = managementUserName;
        this.photoUrl = photoUrl;
        this.photoUrls = photoUrls;
        this.finishScheduleDate = finishScheduleDate;
        this.total = total;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public LocalDateTime getFinishScheduleDate() {
        return finishScheduleDate;
    }

    public void setFinishScheduleDate(LocalDateTime finishScheduleDate) {
        this.finishScheduleDate = finishScheduleDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
