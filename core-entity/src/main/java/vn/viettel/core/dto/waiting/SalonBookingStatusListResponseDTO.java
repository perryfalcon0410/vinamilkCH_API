package vn.viettel.core.dto.waiting;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SalonBookingStatusListResponseDTO {

    private Long statusId;

    private String statusName;

    private Long numberOfCustomer;

    private List<SalonBookingStatusListDetailResponseDTO> bookings;

    public SalonBookingStatusListResponseDTO() {
    }

    public SalonBookingStatusListResponseDTO(Long statusId, List<SalonBookingStatusListDetailResponseDTO> bookings) {
        this.statusId = statusId;
        this.bookings = bookings;
        if (!CollectionUtils.isEmpty(bookings)) {
            this.statusName = bookings.get(0).getStatusName();
            this.numberOfCustomer = (long) bookings.stream().collect(
                    Collectors.groupingBy(SalonBookingStatusListDetailResponseDTO::getCustomerId)
            ).size();
        }
    }

    public SalonBookingStatusListResponseDTO(Long statusId, String statusName, List<SalonBookingStatusListDetailResponseDTO> bookings) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.bookings = bookings;
    }

    public SalonBookingStatusListResponseDTO(Long statusId, String statusName, Long numberOfCustomer, List<SalonBookingStatusListDetailResponseDTO> bookings) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.numberOfCustomer = numberOfCustomer;
        this.bookings = bookings;
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

    public Long getNumberOfCustomer() {
        return numberOfCustomer;
    }

    public void setNumberOfCustomer(Long numberOfCustomer) {
        this.numberOfCustomer = numberOfCustomer;
    }

    public List<SalonBookingStatusListDetailResponseDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<SalonBookingStatusListDetailResponseDTO> bookings) {
        this.bookings = bookings;
    }
}
