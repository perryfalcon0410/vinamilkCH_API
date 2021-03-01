package vn.viettel.core.dto.payment;

import java.time.LocalDateTime;

public class NextVisitOfCustomerDTO {
    private Long bookingId;
    private Long salonId;
    private Long receptionId;
    private Long customerId;
    private LocalDateTime nextVisitTime;
    private Long managementUserId;
    private String managementUserName;
    private String menuName;
    private Long menuId;
    private Long amountOfMoney;

    private LocalDateTime candidateFirstMonth;
    private LocalDateTime candidateSecondMonth;
    private LocalDateTime candidateThirdMonth;

    public NextVisitOfCustomerDTO() {
    }

    public NextVisitOfCustomerDTO(Long bookingId, Long salonId, Long receptionId, Long customerId, LocalDateTime nextVisitTime, Long managementUserId, String managementUserName, String menuName, Long menuId, Long amountOfMoney, LocalDateTime candidateFirstMonth, LocalDateTime candidateSecondMonth, LocalDateTime candidateThirdMonth) {
        this.bookingId = bookingId;
        this.salonId = salonId;
        this.receptionId = receptionId;
        this.customerId = customerId;
        this.nextVisitTime = nextVisitTime;
        this.managementUserId = managementUserId;
        this.managementUserName = managementUserName;
        this.menuName = menuName;
        this.menuId = menuId;
        this.amountOfMoney = amountOfMoney;
        this.candidateFirstMonth = candidateFirstMonth;
        this.candidateSecondMonth = candidateSecondMonth;
        this.candidateThirdMonth = candidateThirdMonth;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
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

    public LocalDateTime getNextVisitTime() {
        return nextVisitTime;
    }

    public void setNextVisitTime(LocalDateTime nextVisitTime) {
        this.nextVisitTime = nextVisitTime;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public String getManagementUserName() {
        return managementUserName;
    }

    public void setManagementUserName(String managementUserName) {
        this.managementUserName = managementUserName;
    }


    public Long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public LocalDateTime getCandidateFirstMonth() {
        return candidateFirstMonth;
    }

    public void setCandidateFirstMonth(LocalDateTime candidateFirstMonth) {
        this.candidateFirstMonth = candidateFirstMonth;
    }

    public LocalDateTime getCandidateSecondMonth() {
        return candidateSecondMonth;
    }

    public void setCandidateSecondMonth(LocalDateTime candidateSecondMonth) {
        this.candidateSecondMonth = candidateSecondMonth;
    }

    public LocalDateTime getCandidateThirdMonth() {
        return candidateThirdMonth;
    }

    public void setCandidateThirdMonth(LocalDateTime candidateThirdMonth) {
        this.candidateThirdMonth = candidateThirdMonth;
    }
}
