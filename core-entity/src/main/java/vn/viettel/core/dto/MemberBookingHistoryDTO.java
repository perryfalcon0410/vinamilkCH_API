package vn.viettel.core.dto;

import vn.viettel.core.dto.booking.BookingHistoryResponseDTO;

import java.util.List;

public class MemberBookingHistoryDTO {
    private Long memberId;
    private List<BookingHistoryResponseDTO> bookings;

    public MemberBookingHistoryDTO() {
    }

    public MemberBookingHistoryDTO(Long memberId, List<BookingHistoryResponseDTO> bookings) {
        this.memberId = memberId;
        this.bookings = bookings;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public List<BookingHistoryResponseDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingHistoryResponseDTO> bookings) {
        this.bookings = bookings;
    }
}
