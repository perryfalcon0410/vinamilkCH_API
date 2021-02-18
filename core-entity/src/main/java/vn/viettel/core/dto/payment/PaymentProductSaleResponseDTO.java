package vn.viettel.core.dto.payment;

import vn.viettel.core.db.entity.OrderProductDetail;

import java.util.List;

public class PaymentProductSaleResponseDTO {
    private Long bookingId;
    private Double orderProductTotal= 0.0;
    private List<OrderProductDetail> orderDetails;

    public PaymentProductSaleResponseDTO() {
    }

    public PaymentProductSaleResponseDTO(Long bookingId, Double orderProductTotal, List<OrderProductDetail> orderDetails) {
        this.bookingId = bookingId;
        this.orderProductTotal = orderProductTotal;
        this.orderDetails = orderDetails;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Double getOrderProductTotal() {
        return orderProductTotal;
    }

    public void setOrderProductTotal(Double orderProductTotal) {
        this.orderProductTotal = orderProductTotal;
    }

    public List<OrderProductDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderProductDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
