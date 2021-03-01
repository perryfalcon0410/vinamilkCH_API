package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "management_user_business_hours")
@AttributeOverride(name = "id", column = @Column(name = "management_user_business_hour_id"))
public class ManagementUserBusinessHour extends BaseEntity {
    @Column(name = "management_user_id", nullable = false)
    private Long managementUserId;

    @Column(name = "salon_id")
    private Long salonId;

    @Column(name = "working_status")
    private Long workingStatus;

    @Column(name = "specificDate")
    private LocalDate specificDate;

    @Column(name = "start_hour")
    private LocalTime startHour;

    @Column(name = "end_hour")
    private LocalTime endHour;

    @Column(name = "description")
    private String description;

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(Long workingStatus) {
        this.workingStatus = workingStatus;
    }

    public LocalDate getSpecificDate() {
        return specificDate;
    }

    public void setSpecificDate(LocalDate specificDate) {
        this.specificDate = specificDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
