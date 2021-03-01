package vn.viettel.core.dto.waiting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WaitingScheduleCustomerDetailDTO {

    private Long bookingId;

    private Long receptionId;

    private Long memberId;

    private Long customerId;

    private Long hairdresserId;

    private String firstName;

    private String lastName;

    private String firstKatakanaName;

    private String lastKatakanaName;

    private String customerVisitFrequency;

    private Long status;

    private String statusName;

    private Long bookingReferrer;

    private String bookingReferrerName;

    private LocalDateTime startScheduleDate;

    private LocalDateTime endScheduleDate;

    private LocalDate date;

    private LocalTime time;

    private Long startMilestone;

    private Long endMilestone;

    private Long milestoneLength;

    public WaitingScheduleCustomerDetailDTO() {
    }

    public WaitingScheduleCustomerDetailDTO(Long bookingId, Long receptionId, Long memberId, Long customerId, Long hairdresserId, String firstName, String lastName, String firstKatakanaName, String lastKatakanaName, String customerVisitFrequency, Long status, String statusName, Long bookingReferrer, String bookingReferrerName, LocalDateTime startScheduleDate, LocalDateTime endScheduleDate, LocalDate date, LocalTime time, Long startMilestone, Long endMilestone, Long milestoneLength) {
        this.bookingId = bookingId;
        this.receptionId = receptionId;
        this.memberId = memberId;
        this.customerId = customerId;
        this.hairdresserId = hairdresserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firstKatakanaName = firstKatakanaName;
        this.lastKatakanaName = lastKatakanaName;
        this.customerVisitFrequency = customerVisitFrequency;
        this.status = status;
        this.statusName = statusName;
        this.bookingReferrer = bookingReferrer;
        this.bookingReferrerName = bookingReferrerName;
        this.startScheduleDate = startScheduleDate;
        this.endScheduleDate = endScheduleDate;
        this.date = date;
        this.time = time;
        this.startMilestone = startMilestone;
        this.endMilestone = endMilestone;
        this.milestoneLength = milestoneLength;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstKatakanaName() {
        return firstKatakanaName;
    }

    public void setFirstKatakanaName(String firstKatakanaName) {
        this.firstKatakanaName = firstKatakanaName;
    }

    public String getLastKatakanaName() {
        return lastKatakanaName;
    }

    public void setLastKatakanaName(String lastKatakanaName) {
        this.lastKatakanaName = lastKatakanaName;
    }

    public String getCustomerVisitFrequency() {
        return customerVisitFrequency;
    }

    public void setCustomerVisitFrequency(String customerVisitFrequency) {
        this.customerVisitFrequency = customerVisitFrequency;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getBookingReferrer() {
        return bookingReferrer;
    }

    public void setBookingReferrer(Long bookingReferrer) {
        this.bookingReferrer = bookingReferrer;
    }

    public String getBookingReferrerName() {
        return bookingReferrerName;
    }

    public void setBookingReferrerName(String bookingReferrerName) {
        this.bookingReferrerName = bookingReferrerName;
    }

    public LocalDateTime getStartScheduleDate() {
        return startScheduleDate;
    }

    public void setStartScheduleDate(LocalDateTime startScheduleDate) {
        this.startScheduleDate = startScheduleDate;
    }

    public LocalDateTime getEndScheduleDate() {
        return endScheduleDate;
    }

    public void setEndScheduleDate(LocalDateTime endScheduleDate) {
        this.endScheduleDate = endScheduleDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getStartMilestone() {
        return startMilestone;
    }

    public void setStartMilestone(Long startMilestone) {
        this.startMilestone = startMilestone;
    }

    public Long getEndMilestone() {
        return endMilestone;
    }

    public void setEndMilestone(Long endMilestone) {
        this.endMilestone = endMilestone;
    }

    public Long getMilestoneLength() {
        return milestoneLength;
    }

    public void setMilestoneLength(Long milestoneLength) {
        this.milestoneLength = milestoneLength;
    }

    public Long getHairdresserId() {
        return hairdresserId;
    }

    public void setHairdresserId(Long hairdresserId) {
        this.hairdresserId = hairdresserId;
    }
}
