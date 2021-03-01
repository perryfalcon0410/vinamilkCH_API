package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "members")
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "katakana_last_name")
    private String katakanaLastName;

    @Column(name = "katakana_first_name")
    private String katakanaFirstName;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "email")
    private String email;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "status")
    private boolean status;

    @Column(name = "gender")
    private Byte gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "tel")
    private String tel;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "address")
    private String address;

    @Column(name = "prefecture_id")
    private Long prefectureId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "city")
    private String city;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "channel")
    private String channel;

    @Column(name = "point")
    private Double point;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
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
