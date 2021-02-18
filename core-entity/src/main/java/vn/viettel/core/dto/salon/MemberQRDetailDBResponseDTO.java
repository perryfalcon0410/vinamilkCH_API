package vn.viettel.core.dto.salon;

import java.time.LocalDate;

public interface MemberQRDetailDBResponseDTO {

    Long getMemberId();

    Long getCompanyId();

    Long getCustomerId();

    Long getSalonId();

    String getFirstName();

    String getLastName();

    String getKatakanaLastName();

    String getKatakanaFirstName();

    LocalDate getBirthday();

    String getTel();

    String getQrCode();

    Double getPoint();
}
