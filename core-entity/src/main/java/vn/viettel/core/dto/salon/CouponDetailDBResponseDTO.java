package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;

public interface CouponDetailDBResponseDTO {
    Long getId();

    String getCouponCode();

    String getDescription();

    Double getFactor();

    Long getCouponType();

    LocalDateTime getExpirationDate();

    String getTag();

    LocalDateTime getStartDate();

    Boolean getIsReleased();

    Boolean getIsCombinedUsed();
}
