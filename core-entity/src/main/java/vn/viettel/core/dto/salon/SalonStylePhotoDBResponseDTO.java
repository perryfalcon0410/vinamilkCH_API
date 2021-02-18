package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;

public interface SalonStylePhotoDBResponseDTO {
    Long getSalonStyleId();
    String getPhotoUrl();
    LocalDateTime getDateShot();
}
