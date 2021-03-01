package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_coupon")
public class PointCoupon extends BaseEntity {

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "enable")
    private Boolean enable;

    @Column(name = "type", length = 2)
    private String type;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "number_of_point")
    private Long numberOfPoint;

    @Column(name = "coupon_type", length = 2)
    private String couponType;

    @Column(name = "expiration_month")
    private Long expirationMonth;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "thumbnail_picture_url")
    private String thumbnailPictureUrl;

    public Integer getObject() {
        return object;
    }

    public void setObject(Integer object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getNumberOfPoint() {
        return numberOfPoint;
    }

    public void setNumberOfPoint(Long numberOfPoint) {
        this.numberOfPoint = numberOfPoint;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public Long getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Long expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getThumbnailPictureUrl() {
        return thumbnailPictureUrl;
    }

    public void setThumbnailPictureUrl(String thumbnailPictureUrl) {
        this.thumbnailPictureUrl = thumbnailPictureUrl;
    }
}
