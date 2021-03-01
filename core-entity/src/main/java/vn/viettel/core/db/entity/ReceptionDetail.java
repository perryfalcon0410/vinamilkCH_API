package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reception_details")
@AttributeOverride(name = "id", column = @Column(name = "reception_detail_id"))
public class ReceptionDetail extends BaseEntity {
    @Column(name = "reception_id", nullable = false)
    private Long receptionId;

    @Column(name = "salon_menu_id", nullable = false)
    private Long salonMenuId;

    @Column(name = "amount")
    private Long amount;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getSalonMenuId() {
        return salonMenuId;
    }

    public void setSalonMenuId(Long salonMenuId) {
        this.salonMenuId = salonMenuId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
