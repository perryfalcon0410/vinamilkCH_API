package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_member")
public class ShopMember extends BaseEntity {

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "qr_code_picture")
    private String qrCodePicture;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    @Column(name = "number_of_point")
    private Long numberOfPoint;

    @Column(name = "last_point_acquisition_date")
    private LocalDateTime lastPointAcquisitionDate;

    @Column(name = "opt_in")
    private Boolean opt_in;

    @Column(name = "status")
    private String status;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
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

    public String getQrCodePicture() {
        return qrCodePicture;
    }

    public void setQrCodePicture(String qrCodePicture) {
        this.qrCodePicture = qrCodePicture;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Long getNumberOfPoint() {
        return numberOfPoint;
    }

    public void setNumberOfPoint(Long numberOfPoint) {
        this.numberOfPoint = numberOfPoint;
    }

    public LocalDateTime getLastPointAcquisitionDate() {
        return lastPointAcquisitionDate;
    }

    public void setLastPointAcquisitionDate(LocalDateTime lastPointAcquisitionDate) {
        this.lastPointAcquisitionDate = lastPointAcquisitionDate;
    }

    public Boolean getOpt_in() {
        return opt_in;
    }

    public void setOpt_in(Boolean opt_in) {
        this.opt_in = opt_in;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
