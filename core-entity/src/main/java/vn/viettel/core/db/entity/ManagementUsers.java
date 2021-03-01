package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "management_users")
@AttributeOverride(name = "id", column = @Column(name = "management_user_id"))
public class ManagementUsers extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @Column(name = "gender")
    private Byte gender;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "tel")
    private String tel;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "address")
    private String address;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "salon_id")
    private Long salonId;

    @Column(name = "description")
    private String description;

    @Column(name = "beautician_cost")
    private Double beauticianCost;

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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Double getBeauticianCost() {
        return beauticianCost;
    }

    public void setBeauticianCost(Double beauticianCost) {
        this.beauticianCost = beauticianCost;
    }
}
