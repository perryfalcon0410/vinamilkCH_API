package vn.viettel.core.dto.payment;

import java.time.LocalDateTime;
import java.util.List;

public class NextVisitOfCustomerResponseDTO {
    private LocalDateTime candidateFirstMonth;
    private LocalDateTime candidateSecondMonth;
    private LocalDateTime candidateThirdMonth;
    private List<NextVisitOfCustomerDTO> nextVisitOfCustomer;

    public NextVisitOfCustomerResponseDTO() {
    }

    public NextVisitOfCustomerResponseDTO(LocalDateTime candidateFirstMonth, LocalDateTime candidateSecondMonth, LocalDateTime candidateThirdMonth, List<NextVisitOfCustomerDTO> nextVisitOfCustomer) {
        this.candidateFirstMonth = candidateFirstMonth;
        this.candidateSecondMonth = candidateSecondMonth;
        this.candidateThirdMonth = candidateThirdMonth;
        this.nextVisitOfCustomer = nextVisitOfCustomer;
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

    public List<NextVisitOfCustomerDTO> getNextVisitOfCustomer() {
        return nextVisitOfCustomer;
    }

    public void setNextVisitOfCustomer(List<NextVisitOfCustomerDTO> nextVisitOfCustomer) {
        this.nextVisitOfCustomer = nextVisitOfCustomer;
    }
}
