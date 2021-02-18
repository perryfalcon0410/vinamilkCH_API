package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "salon_business_hours")
@AttributeOverride(name = "id", column = @Column(name = "salon_business_hour_id"))
public class SalonBusinessHour extends BaseEntity {
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "business_hour_type", nullable = false)
    private Long businessHourType;

    @Column(name = "start_hour")
    private LocalTime startHour;

    @Column(name = "end_hour")
    private LocalTime endHour;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getBusinessHourType() {
        return businessHourType;
    }

    public void setBusinessHourType(Long businessHourType) {
        this.businessHourType = businessHourType;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }
}
