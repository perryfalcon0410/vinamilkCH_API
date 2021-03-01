package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_categories")
@AttributeOverride(name = "id", column = @Column(name = "salon_category_id"))
public class SalonCategory extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "position")
    private Long position;

    public SalonCategory(String name) {
        this.name = name;
    }

    public SalonCategory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
