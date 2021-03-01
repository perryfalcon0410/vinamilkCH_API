package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequestDTO {
    private Long salonId;

    private String salonSlug;

    private Long memberId;

    private String memberQrCode;

    private List<Long> salonMenuIds;

    private LocalDateTime bookingDate;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<Long> getSalonMenuIds() {
        return salonMenuIds;
    }

    public void setSalonMenuIds(List<Long> salonMenuIds) {
        this.salonMenuIds = salonMenuIds;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public String getMemberQrCode() {
        return memberQrCode;
    }

    public void setMemberQrCode(String memberQrCode) {
        this.memberQrCode = memberQrCode;
    }
}
