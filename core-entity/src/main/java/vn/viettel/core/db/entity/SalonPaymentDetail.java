package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_payment_details")
@AttributeOverride(name = "id", column = @Column(name = "salon_payment_detail_id"))
public class SalonPaymentDetail extends BaseEntity {
    @Column(name = "salon_payment_id", nullable = false)
    private Long salonPaymentId;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "status", nullable = false)
    private Boolean status;

    public Long getSalonPaymentId() {
        return salonPaymentId;
    }

    public void setSalonPaymentId(Long salonPaymentId) {
        this.salonPaymentId = salonPaymentId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
