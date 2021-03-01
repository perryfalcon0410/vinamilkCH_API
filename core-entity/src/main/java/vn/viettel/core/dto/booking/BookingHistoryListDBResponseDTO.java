package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;

public interface BookingHistoryListDBResponseDTO {
    Long getBookingId();

    Long getSalonId();

    String getSalonMenuId();

    Long getManagementUserId();

    LocalDateTime getFinishScheduleDate();

    Double getTotal();

    String getQrCode();
}
