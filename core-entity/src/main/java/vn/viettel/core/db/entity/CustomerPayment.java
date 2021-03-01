package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "customer_payments")
public class CustomerPayment extends BaseEntity {

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "price", precision = 9, scale = 2)
    private BigDecimal price;

    @Column(name = "sub_total_price", precision = 9, scale = 2)
    private BigDecimal subTotalPrice;

    @Column(name = "tax_price", precision = 9, scale = 2)
    private BigDecimal taxPrice;

    @Column(name = "total_price", precision = 9, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "incentive_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal incentiveRate;

    @Column(name = "charge_id")
    private String chargeId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "payjp_customer_id")
    private String payjpCustomerId;

    @Column(name = "total_platform_fee")
    private BigDecimal totalPatformFee;

    @Column(name = "platform_fee")
    private BigDecimal platformFee;

    @Column(name = "platform_fee_rate")
    private BigDecimal platformFeeRate;

    @Column(name = "redunded_amount")
    private BigDecimal redundedAmount;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubTotalPrice() {
        return subTotalPrice;
    }

    public void setSubTotalPrice(BigDecimal subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    public BigDecimal getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(BigDecimal taxPrice) {
        this.taxPrice = taxPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getIncentiveRate() {
        return incentiveRate;
    }

    public void setIncentiveRate(BigDecimal incentiveRate) {
        this.incentiveRate = incentiveRate;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Integer getObject() {
        return object;
    }

    public void setObject(Integer object) {
        this.object = object;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPayjpCustomerId() {
        return payjpCustomerId;
    }

    public void setPayjpCustomerId(String payjpCustomerId) {
        this.payjpCustomerId = payjpCustomerId;
    }

    public BigDecimal getTotalPatformFee() {
        return totalPatformFee;
    }

    public void setTotalPatformFee(BigDecimal totalPatformFee) {
        this.totalPatformFee = totalPatformFee;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getPlatformFeeRate() {
        return platformFeeRate;
    }

    public void setPlatformFeeRate(BigDecimal platformFeeRate) {
        this.platformFeeRate = platformFeeRate;
    }

    public BigDecimal getRedundedAmount() {
        return redundedAmount;
    }

    public void setRedundedAmount(BigDecimal redundedAmount) {
        this.redundedAmount = redundedAmount;
    }
}
