package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@AttributeOverride(name = "id", column = @Column(name = "holiday_id"))
public class Holiday extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "business_hour_type_id")
    private Long businessHourTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getBusinessHourTypeId() {
        return businessHourTypeId;
    }

    public void setBusinessHourTypeId(Long businessHourTypeId) {
        this.businessHourTypeId = businessHourTypeId;
    }
}
