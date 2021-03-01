package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "receptions")
@AttributeOverride(name = "id", column = @Column(name = "reception_id"))
public class Reception extends BaseEntity {

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "management_user_id", nullable = false)
    private Long managementUserId;

    @Column(name = "salon_style_id", nullable = false)
    private Long salonStyleId;

    @Column(name = "salon_design_id")
    private Long salonDesignId;

    @Column(name = "total_without_tax")
    private Double totalWithoutTax = 0.0;

    @Column(name = "total")
    private Double total = 0.0;

    @Column(name = "aftermath_cost")
    private Double aftermathCost;

    @Column(name = "aftermath_total")
    private Double aftermathTotal;

    @Column(name = "order_product_total")
    private Double orderProductTotal = 0.0;

    @Column(name = "order_menu_total")
    private Double orderMenuTotal = 0.0;

    @Column(name = "order_direct_total")
    private Double orderDirectTotal = 0.0;

    @Column(name = "coupon_total")
    private Double couponTotal = 0.0;

    @Column(name = "point_used")
    private Double pointUsed;

    @Column(name = "point_gained")
    private Double pointGained;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public Long getSalonStyleId() {
        return salonStyleId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }

    public Double getTotalWithoutTax() {
        return totalWithoutTax;
    }

    public void setTotalWithoutTax(Double totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getSalonDesignId() {
        return salonDesignId;
    }

    public void setSalonDesignId(Long salonDesignId) {
        this.salonDesignId = salonDesignId;
    }

    public Double getOrderProductTotal() {
        return orderProductTotal;
    }

    public void setOrderProductTotal(Double orderProductTotal) {
        this.orderProductTotal = orderProductTotal;
    }

    public Double getOrderMenuTotal() {
        return orderMenuTotal;
    }

    public void setOrderMenuTotal(Double orderMenuTotal) {
        this.orderMenuTotal = orderMenuTotal;
    }

    public Double getCouponTotal() {
        return couponTotal;
    }

    public void setCouponTotal(Double couponTotal) {
        this.couponTotal = couponTotal;
    }

    public Double getAftermathCost() {
        return aftermathCost;
    }

    public void setAftermathCost(Double aftermathCost) {
        this.aftermathCost = aftermathCost;
    }

    public Double getAftermathTotal() {
        return aftermathTotal;
    }

    public void setAftermathTotal(Double aftermathTotal) {
        this.aftermathTotal = aftermathTotal;
    }

    public Double getPointUsed() {
        return pointUsed;
    }

    public void setPointUsed(Double pointUsed) {
        this.pointUsed = pointUsed;
    }

    public Double getPointGained() {
        return pointGained;
    }

    public void setPointGained(Double pointGained) {
        this.pointGained = pointGained;
    }

    public Double getOrderDirectTotal() {
        return orderDirectTotal;
    }

    public void setOrderDirectTotal(Double orderDirectTotal) {
        this.orderDirectTotal = orderDirectTotal;
    }
}
