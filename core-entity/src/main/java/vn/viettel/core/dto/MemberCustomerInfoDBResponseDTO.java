package vn.viettel.core.dto;

import java.time.LocalDate;

public interface MemberCustomerInfoDBResponseDTO {
    Long getMemberId();

    String getMemberFirstName();

    String getMemberLastName();

    String getMemberKatakanaFirstName();

    String getMemberKatakanaLastName();

    Long getCompanyId();

    String getMemberEmail();

    Byte getMemberGender();

    LocalDate getMemberBirthday();

    String getMemberTel();

    String getMemberQrCode();

    Long getCustomerId();

    String getCustomerFirstName();

    String getCustomerLastName();

    String getCustomerKatakanaFirstName();

    String getCustomerKatakanaLastName();

    Long getSalonId();

    Byte getCustomerGender();

    LocalDate getCustomerBirthday();

    String getCustomerTel();

    String getCustomerQrCode();
}
