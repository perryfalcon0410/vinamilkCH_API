package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_style_type_details")
@AttributeOverride(name = "id", column = @Column(name = "salon_style_type_detail_id"))
public class SalonStyleTypeDetail extends BaseEntity {

    @Column(name = "salon_style_group_id", nullable = false)
    private Long salonStyleGroupId;

    @Column(name = "salon_style_type_id", nullable = false)
    private Long salonStyleTypeId;

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public Long getSalonStyleTypeId() {
        return salonStyleTypeId;
    }

    public void setSalonStyleTypeId(Long salonStyleTypeId) {
        this.salonStyleTypeId = salonStyleTypeId;
    }
}
