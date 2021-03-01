package vn.viettel.core.dto.reception;

import vn.viettel.core.dto.salon.SalonCouponSaveResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class CouponInfoManagementDTO {
    private String couponCode;
    private String description;
    private Long couponType;
    private Long applicationStore;
    private Boolean isCombinedUsed;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private Byte discountType;
    private Double factor = 0.0;
    private Double direct;
    private String product;
    private List<SalonCouponSaveResponseDTO> couponAppStore;
    private String discountRate;

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

    public Long getCouponType() {
        return couponType;
    }

    public void setCouponType(Long couponType) {
        this.couponType = couponType;
    }

    public Long getApplicationStore() {
        return applicationStore;
    }

    public void setApplicationStore(Long applicationStore) {
        this.applicationStore = applicationStore;
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

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Double getDirect() {
        return direct;
    }

    public void setDirect(Double direct) {
        this.direct = direct;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Byte getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Byte discountType) {
        this.discountType = discountType;
    }

    public List<SalonCouponSaveResponseDTO> getCouponAppStore() {
        return couponAppStore;
    }

    public void setCouponAppStore(List<SalonCouponSaveResponseDTO> couponAppStore) {
        this.couponAppStore = couponAppStore;
    }

    public String getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(String discountRate) {
        this.discountRate = discountRate;
    }
}
