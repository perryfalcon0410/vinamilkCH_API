package vn.viettel.core.dto;

import java.util.List;

public class MemberBookingHistoryResponseDTO {
    private Long memberId;
    private String customerId;
    private List<Long> customerIds;

    public MemberBookingHistoryResponseDTO() {
    }

    public MemberBookingHistoryResponseDTO(Long memberId, String customerId, List<Long> customerIds) {
        this.memberId = memberId;
        this.customerId = customerId;
        this.customerIds = customerIds;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Long> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(List<Long> customerIds) {
        this.customerIds = customerIds;
    }
}
