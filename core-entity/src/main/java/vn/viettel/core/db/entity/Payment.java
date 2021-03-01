package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_price", precision = 9, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "payment_month")
    private LocalDate paymentMonth;

    @Column(name = "status", nullable = false)
    private Integer status;

    public Payment() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(LocalDate paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
