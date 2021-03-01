package vn.viettel.core.dto.booking;

import java.time.LocalDateTime;

public interface BookingHistoryDBResponseDTO {
    Long getBookingId();

    LocalDateTime getFinishScheduleDate();

    String getPhotoUrls();
}
