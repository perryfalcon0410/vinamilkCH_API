package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_program")
public class GroupProgram extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "event_reservation_id", nullable = false)
    private Long eventReservationId;

    @Column(name = "sub_event_name", nullable = false)
    private String subEventName;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "place")
    private String place;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "price")
    private Long price;

    @Column(name = "unit_price")
    private Long unitPrice;

    @Column(name = "Lecturer")
    private String lecturer;

    @Column(name = "recruitment_number", nullable = false)
    private Long recruitmentNumber;

    @Column(name = "application_button", nullable = false)
    private String applicationButton;

    @Column(name = "application_start_date")
    private LocalDateTime applicationStartDate;

    @Column(name = "application_end_date")
    private LocalDateTime applicationEndDate;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getEventReservationId() {
        return eventReservationId;
    }

    public void setEventReservationId(Long eventReservationId) {
        this.eventReservationId = eventReservationId;
    }

    public String getSubEventName() {
        return subEventName;
    }

    public void setSubEventName(String subEventName) {
        this.subEventName = subEventName;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public Long getRecruitmentNumber() {
        return recruitmentNumber;
    }

    public void setRecruitmentNumber(Long recruitmentNumber) {
        this.recruitmentNumber = recruitmentNumber;
    }

    public String getApplicationButton() {
        return applicationButton;
    }

    public void setApplicationButton(String applicationButton) {
        this.applicationButton = applicationButton;
    }

    public LocalDateTime getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDateTime applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDateTime getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(LocalDateTime applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }
}
