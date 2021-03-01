package vn.viettel.core.dto.waiting;

public class CustomerVisitedInfoResponseDTO {

    private Long customerId;

    private Long numberOfVisited;

    public CustomerVisitedInfoResponseDTO() {
    }

    public CustomerVisitedInfoResponseDTO(Long customerId, Long numberOfVisited) {
        this.customerId = customerId;
        this.numberOfVisited = numberOfVisited;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getNumberOfVisited() {
        return numberOfVisited;
    }

    public void setNumberOfVisited(Long numberOfVisited) {
        this.numberOfVisited = numberOfVisited;
    }
}
