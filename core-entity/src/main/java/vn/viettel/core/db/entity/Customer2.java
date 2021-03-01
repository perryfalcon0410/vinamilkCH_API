package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@AttributeOverride(name = "id", column = @Column(name = "customer_id"))
public class Customer2 extends BaseEntity {
    @Column(name = "salon_id")
    private Long salonId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "katakana_last_name")
    private String katakanaLastName;

    @Column(name = "katakana_first_name")
    private String katakanaFirstName;

    @Column(name = "gender")
    private Byte gender;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "tel")
    private String tel;

    @Column(name = "address")
    private String address;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "prefectureId")
    private Long prefectureId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "status")
    private Short status;

    @Column(name = "qr_code")
    private String qrCode;

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getKatakanaLastName() {
        return katakanaLastName;
    }

    public void setKatakanaLastName(String katakanaLastName) {
        this.katakanaLastName = katakanaLastName;
    }

    public String getKatakanaFirstName() {
        return katakanaFirstName;
    }

    public void setKatakanaFirstName(String katakanaFirstName) {
        this.katakanaFirstName = katakanaFirstName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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
