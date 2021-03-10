package vn.viettel.core.dto;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.IsNumberOnly;
import vn.viettel.core.validation.annotation.NotBlank;

public class CustomerRegisteringRequest extends BaseRequest {
    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_NAME_MUST_BE_NOT_BLANK)
    private String lastName;

    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_NAME_MUST_BE_NOT_BLANK)
    private String firstName;

    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_FIRST_KATAKANA_NAME_MUST_BE_NOT_BLANK)
    private String katakanaFirstName;

    @NotBlank(responseMessage = ResponseMessage.CUSTOMER_LAST_KATAKANA_NAME_MUST_BE_NOT_BLANK)
    private String katakanaLastName;

    @IsNumberOnly(responseMessage = ResponseMessage.CUSTOMER_BIRTH_DATE_MUST_BE_NUMBER)
    private String birthDay;

    @IsNumberOnly(responseMessage = ResponseMessage.CUSTOMER_BIRTH_MONTH_MUST_BE_NUMBER)
    private String birthMonth;

    @IsNumberOnly(responseMessage = ResponseMessage.CUSTOMER_BIRTH_YEAR_MUST_BE_NUMBER)
    private String birthYear;

    @IsNumberOnly(responseMessage = ResponseMessage.CUSTOMER_TELEPHONE_MUST_BE_NUMBER)
    private String tel;

    private Long salonId;

    private String salonSlug;

    private Byte gender;

    private String zipCode;

    private String address;

    private Long prefectureId;

    private Long cityId;

    private Long placeId;

    private String city;

    private Long memberId;

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

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
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

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
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

    public String getSalonSlug() {
        return salonSlug;
    }

    public void setSalonSlug(String salonSlug) {
        this.salonSlug = salonSlug;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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
