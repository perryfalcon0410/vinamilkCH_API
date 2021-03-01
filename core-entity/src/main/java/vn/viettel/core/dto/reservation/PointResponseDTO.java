package vn.viettel.core.dto.reservation;

import vn.viettel.core.dto.booking.BookingPointSalonHistoryResponseDTO;

import java.util.List;

public class PointResponseDTO {
    private Long memberId;

    private Double totalPoint;

    private List<BookingPointSalonHistoryResponseDTO> pointDetails;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Double totalPoint) {
        this.totalPoint = totalPoint;
    }

    public List<BookingPointSalonHistoryResponseDTO> getPointDetails() {
        return pointDetails;
    }

    public void setPointDetails(List<BookingPointSalonHistoryResponseDTO> pointDetails) {
        this.pointDetails = pointDetails;
    }
}
