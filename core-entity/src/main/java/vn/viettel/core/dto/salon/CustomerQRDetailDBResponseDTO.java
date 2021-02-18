package vn.viettel.core.dto.salon;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface CustomerQRDetailDBResponseDTO {
    @Value("{#target.customer_id}")
    Long getId();

    @Value("{#target.salon_id}")
    Long getSalonId();

    @Value("{#target.first_name}")
    String getFirstName();

    @Value("{#target.last_name}")
    String getLastName();

    @Value("{#target.katakana_last_name}")
    String getKatakanaLastName();

    @Value("{#target.katakana_first_name}")
    String getKatakanaFirstName();

    @Value("{#target.birthday}")
    LocalDateTime getBirthday();

    @Value("{#target.tel}")
    String getTel();
}
