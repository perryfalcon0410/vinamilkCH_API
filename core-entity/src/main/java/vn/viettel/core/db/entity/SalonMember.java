package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_members")
@AttributeOverride(name = "id", column = @Column(name = "salon_member_id"))
public class SalonMember extends BaseEntity {

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
