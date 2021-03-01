package vn.viettel.core.dto.waiting;

import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

import java.time.LocalDateTime;

public class SalonBookingWaitingDetailResponseDTO {
    private Long receptionId;

    private Long bookingId;

    private String bookingCode;

    private Long customerId;

    private Long status;

    private Long bookingReferrer;

    private LocalDateTime startScheduleDate;

    private LocalDateTime endScheduleDate;

    private Long salonId;

    private String firstName;

    private String lastName;

    private String firstKatakanaName;

    private String lastKatakanaName;

    private Byte gender;

    private String qrCustomerCode;

    private String qrMemberCode;

    private String customerVisitFrequency;

    private String statusName;

    private String bookingReferrerName;

    private SalonConfirmationDetailDTO lastVisited;

    private SalonConfirmationDetailDTO currentVisited;

    public SalonBookingWaitingDetailResponseDTO() {
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getBookingReferrer() {
        return bookingReferrer;
    }

    public void setBookingReferrer(Long bookingReferrer) {
        this.bookingReferrer = bookingReferrer;
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

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getQrCustomerCode() {
        return qrCustomerCode;
    }

    public void setQrCustomerCode(String qrCustomerCode) {
        this.qrCustomerCode = qrCustomerCode;
    }

    public String getQrMemberCode() {
        return qrMemberCode;
    }

    public void setQrMemberCode(String qrMemberCode) {
        this.qrMemberCode = qrMemberCode;
    }

    public String getCustomerVisitFrequency() {
        return customerVisitFrequency;
    }

    public void setCustomerVisitFrequency(String customerVisitFrequency) {
        this.customerVisitFrequency = customerVisitFrequency;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getBookingReferrerName() {
        return bookingReferrerName;
    }

    public void setBookingReferrerName(String bookingReferrerName) {
        this.bookingReferrerName = bookingReferrerName;
    }

    public SalonConfirmationDetailDTO getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(SalonConfirmationDetailDTO lastVisited) {
        this.lastVisited = lastVisited;
    }

    public SalonConfirmationDetailDTO getCurrentVisited() {
        return currentVisited;
    }

    public void setCurrentVisited(SalonConfirmationDetailDTO currentVisited) {
        this.currentVisited = currentVisited;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }
}
