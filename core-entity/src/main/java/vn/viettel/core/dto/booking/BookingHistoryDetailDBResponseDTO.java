package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;

public interface BookingHistoryDetailDBResponseDTO {
    Long getBookingId();

    Long getSalonId();

    String getSalonMenuId();

    Long getManagementUserId();

    String getPhotoUrl();

    LocalDateTime getFinishScheduleDate();

    Double getTotal();
}
