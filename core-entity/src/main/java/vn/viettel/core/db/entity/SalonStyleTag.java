package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_style_tags")
@AttributeOverride(name = "id", column = @Column(name = "salon_style_tag_id"))
public class SalonStyleTag extends BaseEntity {

    @Column(name = "salon_tag_id", nullable = false)
    private Long salonTagId;

    @Column(name = "salon_style_id", nullable = false)
    private Long salonStyleId;

    public Long getSalonTagId() {
        return salonTagId;
    }

    public void setSalonTagId(Long salonTagId) {
        this.salonTagId = salonTagId;
    }

    public Long getSalonStyleId() {
        return salonStyleId;
    }

    public void setSalonStyleId(Long salonStyleId) {
        this.salonStyleId = salonStyleId;
    }
}
