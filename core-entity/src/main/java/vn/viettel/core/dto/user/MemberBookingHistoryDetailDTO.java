package vn.viettel.core.dto.user;

import vn.viettel.core.dto.booking.BookingHistoryDetailResponseDTO;

import java.util.List;

public class MemberBookingHistoryDetailDTO {
    private Long memberId;
    private List<BookingHistoryDetailResponseDTO> bookingDetail;

    public MemberBookingHistoryDetailDTO() {
    }

    public MemberBookingHistoryDetailDTO(Long memberId, List<BookingHistoryDetailResponseDTO> bookingDetail) {
        this.memberId = memberId;
        this.bookingDetail = bookingDetail;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<BookingHistoryDetailResponseDTO> getBookingDetail() {
        return bookingDetail;
    }

    public void setBookingDetail(List<BookingHistoryDetailResponseDTO> bookingDetail) {
        this.bookingDetail = bookingDetail;
    }
}
