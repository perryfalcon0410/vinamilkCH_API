package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "coupon_types")
@AttributeOverride(name = "id", column = @Column(name = "coupon_type_id"))
public class CouponType extends BaseEntity {

    @Column(name = "tag", nullable = false)
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
