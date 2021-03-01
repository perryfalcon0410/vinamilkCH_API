package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "group_setting_days")
public class GroupSettingDay extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "open_status", nullable = false)
    private boolean openStatus;

    @Column(name = "weekday", nullable = false)
    private Integer weekday;

    @Column(name = "opening_hour", nullable = false)
    private LocalTime openingHour;

    @Column(name = "closed_hour", nullable = false)
    private LocalTime closedHour;

    @Column(name = "breaktime_start", nullable = false)
    private LocalTime breaktimeStart;

    @Column(name = "breaktime_end", nullable = false)
    private LocalTime breaktimeEnd;

    @Column(name = "available_time", nullable = false, length = 96)
    private String availableTime;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public boolean isOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(boolean openStatus) {
        this.openStatus = openStatus;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public LocalTime getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(LocalTime openingHour) {
        this.openingHour = openingHour;
    }

    public LocalTime getClosedHour() {
        return closedHour;
    }

    public void setClosedHour(LocalTime closedHour) {
        this.closedHour = closedHour;
    }

    public LocalTime getBreaktimeStart() {
        return breaktimeStart;
    }

    public void setBreaktimeStart(LocalTime breaktimeStart) {
        this.breaktimeStart = breaktimeStart;
    }

    public LocalTime getBreaktimeEnd() {
        return breaktimeEnd;
    }

    public void setBreaktimeEnd(LocalTime breaktimeEnd) {
        this.breaktimeEnd = breaktimeEnd;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }
}
