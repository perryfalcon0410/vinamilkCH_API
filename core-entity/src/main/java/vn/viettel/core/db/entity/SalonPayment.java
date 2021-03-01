package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_payments")
@AttributeOverride(name = "id", column = @Column(name = "salon_payment_id"))
public class SalonPayment extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private Long type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }
}
