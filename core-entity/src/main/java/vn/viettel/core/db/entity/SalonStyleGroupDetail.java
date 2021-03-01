package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_style_group_details")
@AttributeOverride(name = "id", column = @Column(name = "salon_style_group_detail_id"))
public class SalonStyleGroupDetail extends BaseEntity {

    @Column(name = "salon_style_group_id", nullable = false)
    private Long salonStyleGroupId;

    @Column(name = "salon_style_id", nullable = false)
    private Long salonStyleId;

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public Long getSalonStyleId() {
        return salonStyleId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }
}
