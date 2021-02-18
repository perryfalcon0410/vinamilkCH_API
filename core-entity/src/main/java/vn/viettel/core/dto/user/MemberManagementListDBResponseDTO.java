package vn.viettel.core.dto.user;

public interface MemberManagementListDBResponseDTO {
    Long getId();

    String getIdStr();

    Long getMemberTypeId();

    String getMemberTypeName();

    String getFirstName();

    String getLastName();

    String getKatakanaLastName();

    String getKatakanaFirstName();

    Byte getGender();

    String getGenderStr();

    Double getPoint();
}
