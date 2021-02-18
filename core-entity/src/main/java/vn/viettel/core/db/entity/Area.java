package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "areas")
@AttributeOverride(name = "id", column = @Column(name = "area_id"))
public class Area extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "position")
    private Long position;

    public Area() {
    }

    public Area(String name) {
        this.name = name;
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
