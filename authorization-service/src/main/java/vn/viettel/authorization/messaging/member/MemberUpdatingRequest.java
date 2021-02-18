package vn.viettel.authorization.messaging.member;

import vn.viettel.core.dto.user.ChannelTypeResponseDTO;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.*;
import vn.viettel.core.validation.annotation.*;

import java.util.List;

public class MemberUpdatingRequest extends BaseRequest {
    private Long memberId;

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

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_BLANK)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage = ResponseMessage.SALON_SLUG_MUST_BE_NOT_NULL)
    @NotBlank(responseMessage = ResponseMessage.SALON_SLUG_MUST_BE_NOT_BLANK)
    @Slug(responseMessage = ResponseMessage.SALON_SLUG_FORMAT_INCORRECT)
    private String salonSlug;

    private String password;

    private Byte gender;

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

    private Long cityId;

    private Long placeId;

    private Long prefectureId;


    private String photoUrl;

    private List<ChannelTypeResponseDTO> channel;

    private Boolean isPasswordChanged;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public List<ChannelTypeResponseDTO> getChannel() {
        return channel;
    }

    public void setChannel(List<ChannelTypeResponseDTO> channel) {
        this.channel = channel;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Boolean isPasswordChanged() {
        return isPasswordChanged;
    }

    public void setIsPasswordChanged(Boolean passwordChanged) {
        this.isPasswordChanged = passwordChanged;
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
