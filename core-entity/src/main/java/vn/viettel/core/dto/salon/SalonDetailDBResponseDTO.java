package vn.viettel.core.dto.salon;

import java.math.BigDecimal;

public interface SalonDetailDBResponseDTO {

    Long getSalonId();

    String getName();

    String getSlug();

    String getTel();

    String getAddress();

    String getRegularHoliday();

    Short getParkingLot();

    String getPaymentMethod();

    String getNearestStation();

    String getWebsite();

    BigDecimal getLatitude();

    BigDecimal getLongtitude();

    String getPhotoUrl();

    String getCommitmentCondition();

    String getFacebook();

    String getTwitter();

    String getInstagram();
}
