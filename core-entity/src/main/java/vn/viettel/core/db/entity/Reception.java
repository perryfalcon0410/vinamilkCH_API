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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }

    public void setTotalWithoutTax(Double totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setSalonDesignId(Long salonDesignId) {
        this.salonDesignId = salonDesignId;
    }

    public void setOrderProductTotal(Double orderProductTotal) {
        this.orderProductTotal = orderProductTotal;
    }

    public void setOrderMenuTotal(Double orderMenuTotal) {
        this.orderMenuTotal = orderMenuTotal;
    }

    public void setCouponTotal(Double couponTotal) {
        this.couponTotal = couponTotal;
    }

    public void setAftermathCost(Double aftermathCost) {
        this.aftermathCost = aftermathCost;
    }

    public void setAftermathTotal(Double aftermathTotal) {
        this.aftermathTotal = aftermathTotal;
    }

    public void setPointUsed(Double pointUsed) {
        this.pointUsed = pointUsed;
    }

    public void setPointGained(Double pointGained) {
        this.pointGained = pointGained;
    }

    public void setOrderDirectTotal(Double orderDirectTotal) {
        this.orderDirectTotal = orderDirectTotal;
    }
}
