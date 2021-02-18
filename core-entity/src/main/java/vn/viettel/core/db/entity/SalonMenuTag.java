package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_menu_tags")
@AttributeOverride(name = "id", column = @Column(name = "salon_menu_tag_id"))
public class SalonMenuTag extends BaseEntity {

    @Column(name = "salon_tag_id", nullable = false)
    private Long salonTagId;

    @Column(name = "salon_menu_id", nullable = false)
    private Long salonMenuId;

    public Long getSalonTagId() {
        return salonTagId;
    }

    public void setSalonTagId(Long salonTagId) {
        this.salonTagId = salonTagId;
    }

    public Long getSalonMenuId() {
        return salonMenuId;
    }

    public void setSalonMenuId(Long salonMenuId) {
        this.salonMenuId = salonMenuId;
    }
}
