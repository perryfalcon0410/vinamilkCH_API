package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_coupon_history")
public class PointCouponHistory extends BaseEntity {

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "point_coupon_id")
    private Long pointCouponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "type_history")
    private String typeHistory;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "number_of_point")
    private Long numberOfPoint;

    @Column(name = "remain_point")
    private Long remainPoint;

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

    public Long getPointCouponId() {
        return pointCouponId;
    }

    public void setPointCouponId(Long pointCouponId) {
        this.pointCouponId = pointCouponId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getTypeHistory() {
        return typeHistory;
    }

    public void setTypeHistory(String typeHistory) {
        this.typeHistory = typeHistory;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getNumberOfPoint() {
        return numberOfPoint;
    }

    public void setNumberOfPoint(Long numberOfPoint) {
        this.numberOfPoint = numberOfPoint;
    }

    public Long getRemainPoint() {
        return remainPoint;
    }

    public void setRemainPoint(Long remainPoint) {
        this.remainPoint = remainPoint;
    }
}
