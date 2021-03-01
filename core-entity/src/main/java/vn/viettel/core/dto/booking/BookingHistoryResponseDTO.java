package vn.viettel.core.dto.booking;

import vn.viettel.core.db.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

public class BookingHistoryResponseDTO extends BaseEntity {
    private Long bookingId;
    private LocalDateTime finishScheduleDate;
    private String photoUrls;
    private List<String> photoUrlList;

    public BookingHistoryResponseDTO() {
    }

    public BookingHistoryResponseDTO(Long bookingId, LocalDateTime finishScheduleDate, String photoUrls, List<String> photoUrlList) {
        this.bookingId = bookingId;
        this.finishScheduleDate = finishScheduleDate;
        this.photoUrls = photoUrls;
        this.photoUrlList = photoUrlList;
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

    public String getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(String photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<String> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(List<String> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }
}
