package vn.viettel.core.dto.payment;

import java.time.LocalDateTime;

public interface NextVisitOfCustomerDBResponseDTO {
    Long getBookingId();
    Long getSalonId();
    Long getReceptionId();
    Long getCustomerId();
    LocalDateTime getNextVisitTime();
    Long getManagementUserId();

    String getManagementUserName();
    String getMenuName();
    Long getMenuId();
    Long getAmountOfMoney();

    LocalDateTime getCandidateFirstMonth();
    LocalDateTime getCandidateSecondMonth();
    LocalDateTime getCandidateThirdMonth();
}
