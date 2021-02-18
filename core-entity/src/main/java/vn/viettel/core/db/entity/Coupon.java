package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
public class Coupon extends BaseEntity {

    @Column(name="coupon_code", nullable = false)
    private String couponCode;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name="factor", nullable = false)
    private Double factor;

    @Column(name="coupon_type", nullable = false)
    private Long couponType;

    @Column(name="expiration_date")
    private LocalDateTime expirationDate;

    @Column(name="is_used")
    private Boolean isUsed;

    @Column(name="is_released")
    private Boolean isReleased;

    @Column(name="is_combined_used")
    private Boolean isCombinedUsed;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "direct")
    private Double direct;

    @Column(name = "discount_type")
    private Byte discountType;

    @Column(name = "product")
    private String product;

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Long getCouponType() {
        return couponType;
    }

    public void setCouponType(Long couponType) {
        this.couponType = couponType;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Boolean getReleased() {
        return isReleased;
    }

    public void setReleased(Boolean released) {
        isReleased = released;
    }

    public Boolean getCombinedUsed() {
        return isCombinedUsed;
    }

    public void setCombinedUsed(Boolean combinedUsed) {
        isCombinedUsed = combinedUsed;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Double getDirect() {
        return direct;
    }

    public void setDirect(Double direct) {
        this.direct = direct;
    }

    public Byte getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Byte discountType) {
        this.discountType = discountType;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
