package vn.viettel.core.dto.salon;

import java.time.LocalDate;

public class MemberQRDetailResponseDTO {
    private Long memberId;

    private Long companyId;

    private Long customerId;

    private Long salonId;

    private String firstName;

    private String lastName;

    private String katakanaLastName;

    private String katakanaFirstName;

    private LocalDate birthday;

    private String tel;

    private String qrCode;

    private Double point;

    public MemberQRDetailResponseDTO() {
    }

    public MemberQRDetailResponseDTO(Long memberId, Long companyId, Long customerId, Long salonId, String firstName, String lastName, String katakanaLastName, String katakanaFirstName, LocalDate birthday, String tel, String qrCode, Double point) {
        this.memberId = memberId;
        this.companyId = companyId;
        this.customerId = customerId;
        this.salonId = salonId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.katakanaLastName = katakanaLastName;
        this.katakanaFirstName = katakanaFirstName;
        this.birthday = birthday;
        this.tel = tel;
        this.qrCode = qrCode;
        this.point = point;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }
}
