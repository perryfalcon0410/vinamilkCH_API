package vn.viettel.core.dto.user;

import vn.viettel.core.dto.booking.BookingHistoryListResponseDTO;

import java.util.List;

public class MemberBookingHistoryListDTO {
    private Long memberId;
    private List<BookingHistoryListResponseDTO> bookingList;

    public MemberBookingHistoryListDTO() {
    }

    public MemberBookingHistoryListDTO(Long memberId, List<BookingHistoryListResponseDTO> bookingList) {
        this.memberId = memberId;
        this.bookingList = bookingList;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<BookingHistoryListResponseDTO> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<BookingHistoryListResponseDTO> bookingList) {
        this.bookingList = bookingList;
    }
}
