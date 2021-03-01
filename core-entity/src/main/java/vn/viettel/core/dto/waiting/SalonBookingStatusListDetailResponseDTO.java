package vn.viettel.core.dto.waiting;

import java.time.LocalDateTime;

public class SalonBookingStatusListDetailResponseDTO {

    private String bookingCode;

    private Long bookingId;

    private Long receptionId;

    private Long customerId;

    private String firstName;

    private String lastName;

    private String firstKatakanaName;

    private String lastKatakanaName;

    private Long hairdresserId;

    private String hairdresserName;

    private LocalDateTime startScheduleDate;

    private LocalDateTime endScheduleDate;

    private Long statusId;

    private String statusName;

    public SalonBookingStatusListDetailResponseDTO() {
    }

    public SalonBookingStatusListDetailResponseDTO(String bookingCode, Long bookingId, Long receptionId, Long customerId, String firstName, String lastName, String firstKatakanaName, String lastKatakanaName, Long hairdresserId, String hairdresserName, LocalDateTime startScheduleDate, LocalDateTime endScheduleDate, Long statusId, String statusName) {
        this.bookingCode = bookingCode;
        this.bookingId = bookingId;
        this.receptionId = receptionId;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firstKatakanaName = firstKatakanaName;
        this.lastKatakanaName = lastKatakanaName;
        this.hairdresserId = hairdresserId;
        this.hairdresserName = hairdresserName;
        this.startScheduleDate = startScheduleDate;
        this.endScheduleDate = endScheduleDate;
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
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

    public Long getHairdresserId() {
        return hairdresserId;
    }

    public void setHairdresserId(Long hairdresserId) {
        this.hairdresserId = hairdresserId;
    }

    public String getHairdresserName() {
        return hairdresserName;
    }

    public void setHairdresserName(String hairdresserName) {
        this.hairdresserName = hairdresserName;
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

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
