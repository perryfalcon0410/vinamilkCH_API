package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "regular_offdays")
@AttributeOverride(name = "id", column = @Column(name = "regular_offday_id"))
public class RegularOffday extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
