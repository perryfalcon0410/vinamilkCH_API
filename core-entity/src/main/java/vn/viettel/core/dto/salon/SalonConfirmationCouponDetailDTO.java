package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;

public class SalonConfirmationCouponDetailDTO {
    private Long id;

    private String couponCode;

    private String description;

    private Double factor;

    private Double subtractValue;

    private LocalDateTime expirationDate;

    private Boolean isCombinedUsed;

    private LocalDateTime startDate;

    public SalonConfirmationCouponDetailDTO() {
    }

    public SalonConfirmationCouponDetailDTO(Long id, String couponCode, String description, Double factor, Double subtractValue, LocalDateTime expirationDate, Boolean isCombinedUsed, LocalDateTime startDate) {
        this.id = id;
        this.couponCode = couponCode;
        this.description = description;
        this.factor = factor;
        this.subtractValue = subtractValue;
        this.expirationDate = expirationDate;
        this.isCombinedUsed = isCombinedUsed;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getSubtractValue() {
        return subtractValue;
    }

    public void setSubtractValue(Double subtractValue) {
        this.subtractValue = subtractValue;
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
}
