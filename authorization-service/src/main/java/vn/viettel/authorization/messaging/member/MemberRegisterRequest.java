package vn.viettel.authorization.messaging.member;

import vn.viettel.core.dto.user.ChannelTypeResponseDTO;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.*;
import vn.viettel.core.validation.annotation.*;

import java.time.LocalDate;
import java.util.List;

public class MemberRegisterRequest extends BaseRequest {
    private Long memberId;

    @NotNull(responseMessage = ResponseMessage.INVALID_TOKEN)
    private String token;

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

    private String name;

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_BLANK)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    private Long companyId;

    @NotNull(responseMessage = ResponseMessage.COMPANY_SLUG_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.COMPANY_SLUG_MUST_BE_NOT_BLANK)
    @Slug(responseMessage = ResponseMessage.COMPANY_SLUG_FORMAT_INCORRECT)
    private String companySlug;

    @NotNull(responseMessage = ResponseMessage.SALON_SLUG_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.SALON_SLUG_MUST_BE_NOT_BLANK)
    @Slug(responseMessage = ResponseMessage.SALON_SLUG_FORMAT_INCORRECT)
    private String salonSlug;

    private String encryptedPassword;

    private Byte gender;

    private LocalDate birthday;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_DATE_MUST_BE_NUMBER)
    private String birthDate;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_MONTH_MUST_BE_NUMBER)
    private String birthMonth;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_BIRTH_YEAR_MUST_BE_NUMBER)
    private String birthYear;

    @IsNumberOnly(responseMessage = ResponseMessage.MEMBER_TELEPHONE_MUST_BE_NUMBER)
    private String tel;

    @NotNull(responseMessage = ResponseMessage.MEMBER_ZIP_CODE_MUST_NOT_NULL)
    private String zipcode;

    private String address;

    private String city;

    private Long prefectureId;

    private Long cityId;

    private Long placeId;

    private String activationCode;

    private String photoUrl;

    private List<ChannelTypeResponseDTO> channel;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public List<ChannelTypeResponseDTO> getChannel() {
        return channel;
    }

    public void setChannel(List<ChannelTypeResponseDTO> channel) {
        this.channel = channel;
    }

    public String getCompanySlug() {
        return companySlug;
    }

    public void setCompanySlug(String companySlug) {
        this.companySlug = companySlug;
    }

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}
