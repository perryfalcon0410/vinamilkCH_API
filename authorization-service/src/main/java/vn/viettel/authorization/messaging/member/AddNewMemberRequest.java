package vn.viettel.authorization.messaging.member;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.IsJapaneseCharactersOnly;
import vn.viettel.core.validation.annotation.IsJapaneseKatakanaCharactersOnly;
import vn.viettel.core.validation.annotation.IsNumberOnly;
import vn.viettel.core.validation.annotation.NotBlank;

public class AddNewMemberRequest {

    private Long salonId;
    private String salonName;

    @NotBlank(responseMessage = ResponseMessage.MEMBER_LAST_NAME_MUST_BE_NOT_BLANK)
    @IsJapaneseCharactersOnly(responseMessage = ResponseMessage.MEMBER_LAST_NAME_MUST_BE_JAPANESE_CHARACTERS)
    private String lastName;

    @NotBlank(responseMessage = ResponseMessage.MEMBER_FIRST_NAME_MUST_BE_NOT_BLANK)
    @IsJapaneseCharactersOnly(responseMessage = ResponseMessage.MEMBER_FIRST_NAME_MUST_BE_JAPANESE_CHARACTERS)
    private String firstName;

    @NotBlank(responseMessage = ResponseMessage.MEMBER_FIRST_KATAKANA_NAME_MUST_BE_NOT_BLANK)
    @IsJapaneseKatakanaCharactersOnly(responseMessage = ResponseMessage.MEMBER_FIRST_KATAKANA_NAME_MUST_BE_JAPANESE_CHARACTERS)
    private String katakanaFirstName;

    @NotBlank(responseMessage = ResponseMessage.MEMBER_LAST_KATAKANA_NAME_MUST_BE_NOT_BLANK)
    @IsJapaneseKatakanaCharactersOnly(responseMessage = ResponseMessage.MEMBER_LAST_KATAKANA_NAME_MUST_BE_JAPANESE_CHARACTERS)
    private String katakanaLastName;

    private Byte gender;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_DATE_MUST_BE_NUMBER)
    private String birthDate;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_MONTH_MUST_BE_NUMBER)
    private String birthMonth;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_YEAR_MUST_BE_NUMBER)
    private String birthYear;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_TELEPHONE_MUST_BE_NUMBER)
    private String tel;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getKatakanaFirstName() {
        return katakanaFirstName;
    }

    public void setKatakanaFirstName(String katakanaFirstName) {
        this.katakanaFirstName = katakanaFirstName;
    }

    public String getKatakanaLastName() {
        return katakanaLastName;
    }

    public void setKatakanaLastName(String katakanaLastName) {
        this.katakanaLastName = katakanaLastName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
