package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_commitment_condition_detail")
@AttributeOverride(name = "id", column = @Column(name = "salon_commitment_condition_detail_id"))
public class SalonCommitmentConditionDetail extends BaseEntity {
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "salon_commitment_condition_id", nullable = false)
    private Long salonCommitmentConditionId;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getSalon_commitment_condition_id() {
        return salonCommitmentConditionId;
    }

    public void setSalon_commitment_condition_id(Long salon_commitment_condition_id) {
        this.salonCommitmentConditionId = salon_commitment_condition_id;
    }
}
