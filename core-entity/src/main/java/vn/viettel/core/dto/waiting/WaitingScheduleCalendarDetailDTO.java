package vn.viettel.core.dto.waiting;

import java.util.List;

public class WaitingScheduleCalendarDetailDTO {

    private Long hairdresserId;

    private String hairdresserName;

    private List<WaitingScheduleCustomerDetailDTO> customers;

    public WaitingScheduleCalendarDetailDTO() {
    }

    public WaitingScheduleCalendarDetailDTO(Long hairdresserId, String hairdresserName, List<WaitingScheduleCustomerDetailDTO> customers) {
        this.hairdresserId = hairdresserId;
        this.hairdresserName = hairdresserName;
        this.customers = customers;
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

    public List<WaitingScheduleCustomerDetailDTO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<WaitingScheduleCustomerDetailDTO> customers) {
        this.customers = customers;
    }
}
