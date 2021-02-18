package vn.viettel.core.db.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_item_schedules")
public class ShopItemSchedule extends BaseEntity {

    @Column(name = "available_time")
    private String availableTime;

    @Column(name = "breaktime_end")
    private String breaktimeEnd;

    @Column(name = "breaktime_start")
    private String breaktimeStart;

    @Column(name = "closed_hour")
    private String closedHour;

    @Column(name = "is_published")
    private int isPublished;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "opening_hour")
    private String openingHour;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    private int status;

    public ShopItemSchedule() {
    }

    public String getAvailableTime() {
        return this.availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public String getBreaktimeEnd() {
        return this.breaktimeEnd;
    }

    public void setBreaktimeEnd(String breaktimeEnd) {
        this.breaktimeEnd = breaktimeEnd;
    }

    public String getBreaktimeStart() {
        return this.breaktimeStart;
    }

    public void setBreaktimeStart(String breaktimeStart) {
        this.breaktimeStart = breaktimeStart;
    }

    public String getClosedHour() {
        return this.closedHour;
    }

    public void setClosedHour(String closedHour) {
        this.closedHour = closedHour;
    }

    public int getIsPublished() {
        return this.isPublished;
    }

    public void setIsPublished(int isPublished) {
        this.isPublished = isPublished;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getOpeningHour() {
        return this.openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public LocalDate getScheduleDate() {
        return this.scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}