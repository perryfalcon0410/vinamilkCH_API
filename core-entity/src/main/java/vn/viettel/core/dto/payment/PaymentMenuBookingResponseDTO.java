package vn.viettel.core.dto.payment;

import vn.viettel.core.db.entity.ReceptionDetail;

import java.util.List;

public class PaymentMenuBookingResponseDTO {
    private Long bookingId;
    private List<ReceptionDetail> receptionDetails;
    private Double orderMenuTotal;

    public PaymentMenuBookingResponseDTO() {
    }

    public PaymentMenuBookingResponseDTO(Long bookingId, List<ReceptionDetail> receptionDetails, Double orderMenuTotal) {
        this.bookingId = bookingId;
        this.receptionDetails = receptionDetails;
        this.orderMenuTotal = orderMenuTotal;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<ReceptionDetail> getReceptionDetails() {
        return receptionDetails;
    }

    public void setReceptionDetails(List<ReceptionDetail> receptionDetails) {
        this.receptionDetails = receptionDetails;
    }

    public Double getOrderMenuTotal() {
        return orderMenuTotal;
    }

    public void setOrderMenuTotal(Double orderMenuTotal) {
        this.orderMenuTotal = orderMenuTotal;
    }
}
