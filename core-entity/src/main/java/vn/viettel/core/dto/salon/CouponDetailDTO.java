package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;

public class CouponDetailDTO {
    private Long id;

    private String couponCode;

    private String description;

    private Double factor;

    private Long couponType;

    private LocalDateTime expirationDate;

    private String tag;

    private LocalDateTime startDate;

    private Boolean checkExpiration;

    private Boolean isUsed;

    private Boolean isReleased;

    private Boolean isCombinedUsed;

    private Byte discountType;

    private Long applicationStore;

    private String product;

    private Double direct;

    public CouponDetailDTO() {
    }

    public CouponDetailDTO(Long id, String couponCode, String description, Double factor, Long couponType, LocalDateTime expirationDate, String tag, LocalDateTime startDate, Boolean checkExpiration, Boolean isUsed, Boolean isReleased, Boolean isCombinedUsed, Byte discountType, Long applicationStore, String product, Double direct) {
        this.id = id;
        this.couponCode = couponCode;
        this.description = description;
        this.factor = factor;
        this.couponType = couponType;
        this.expirationDate = expirationDate;
        this.tag = tag;
        this.startDate = startDate;
        this.checkExpiration = checkExpiration;
        this.isUsed = isUsed;
        this.isReleased = isReleased;
        this.isCombinedUsed = isCombinedUsed;
        this.discountType = discountType;
        this.applicationStore = applicationStore;
        this.product = product;
        this.direct = direct;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Boolean getCheckExpiration() {
        return checkExpiration;
    }

    public void setCheckExpiration(Boolean checkExpiration) {
        this.checkExpiration = checkExpiration;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Boolean getIsReleased() {
        return isReleased;
    }

    public void setIsReleased(Boolean released) {
        isReleased = released;
    }

    public Boolean getIsCombinedUsed() {
        return isCombinedUsed;
    }

    public void setIsCombinedUsed(Boolean combinedUsed) {
        isCombinedUsed = combinedUsed;
    }

    public Byte getDiscountType() {
        return discountType;
    }

    public void setDiscountType(Byte discountType) {
        this.discountType = discountType;
    }

    public Long getApplicationStore() {
        return applicationStore;
    }

    public void setApplicationStore(Long applicationStore) {
        this.applicationStore = applicationStore;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getDirect() {
        return direct;
    }

    public void setDirect(Double direct) {
        this.direct = direct;
    }
}
