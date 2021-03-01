package vn.viettel.core.dto.payment;

import java.time.LocalDateTime;

public class PaymentSaveNextCandidateDateResponseDTO {
    private Long bookingId;
    private LocalDateTime candidateFirstMonth;
    private LocalDateTime candidateSecondMonth;
    private LocalDateTime candidateThirdMonth;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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
