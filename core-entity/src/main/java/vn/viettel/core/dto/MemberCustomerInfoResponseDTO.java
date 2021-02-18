package vn.viettel.core.dto;

import java.time.LocalDate;

public class MemberCustomerInfoResponseDTO {

    private Long memberId;

    private String memberFirstName;

    private String memberLastName;

    private String memberKatakanaFirstName;

    private String memberKatakanaLastName;

    private Long companyId;

    private String memberEmail;

    private Byte memberGender;

    private LocalDate memberBirthday;

    private String memberTel;

    private String memberQrCode;

    private Long customerId;

    private String customerFirstName;

    private String customerLastName;

    private String customerKatakanaFirstName;

    private String customerKatakanaLastName;

    private Long salonId;

    private Byte customerGender;

    private LocalDate customerBirthday;

    private String customerTel;

    private String customerQrCode;

    public MemberCustomerInfoResponseDTO() {
    }

    public MemberCustomerInfoResponseDTO(Long memberId, String memberFirstName, String memberLastName, String memberKatakanaFirstName, String memberKatakanaLastName, Long companyId, String memberEmail, Byte memberGender, LocalDate memberBirthday, String memberTel, String memberQrCode, Long customerId, String customerFirstName, String customerLastName, String customerKatakanaFirstName, String customerKatakanaLastName, Long salonId, Byte customerGender, LocalDate customerBirthday, String customerTel, String customerQrCode) {
        this.memberId = memberId;
        this.memberFirstName = memberFirstName;
        this.memberLastName = memberLastName;
        this.memberKatakanaFirstName = memberKatakanaFirstName;
        this.memberKatakanaLastName = memberKatakanaLastName;
        this.companyId = companyId;
        this.memberEmail = memberEmail;
        this.memberGender = memberGender;
        this.memberBirthday = memberBirthday;
        this.memberTel = memberTel;
        this.memberQrCode = memberQrCode;
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerKatakanaFirstName = customerKatakanaFirstName;
        this.customerKatakanaLastName = customerKatakanaLastName;
        this.salonId = salonId;
        this.customerGender = customerGender;
        this.customerBirthday = customerBirthday;
        this.customerTel = customerTel;
        this.customerQrCode = customerQrCode;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public String getMemberKatakanaFirstName() {
        return memberKatakanaFirstName;
    }

    public void setMemberKatakanaFirstName(String memberKatakanaFirstName) {
        this.memberKatakanaFirstName = memberKatakanaFirstName;
    }

    public String getMemberKatakanaLastName() {
        return memberKatakanaLastName;
    }

    public void setMemberKatakanaLastName(String memberKatakanaLastName) {
        this.memberKatakanaLastName = memberKatakanaLastName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Byte getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(Byte memberGender) {
        this.memberGender = memberGender;
    }

    public LocalDate getMemberBirthday() {
        return memberBirthday;
    }

    public void setMemberBirthday(LocalDate memberBirthday) {
        this.memberBirthday = memberBirthday;
    }

    public String getMemberTel() {
        return memberTel;
    }

    public void setMemberTel(String memberTel) {
        this.memberTel = memberTel;
    }

    public String getMemberQrCode() {
        return memberQrCode;
    }

    public void setMemberQrCode(String memberQrCode) {
        this.memberQrCode = memberQrCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerKatakanaFirstName() {
        return customerKatakanaFirstName;
    }

    public void setCustomerKatakanaFirstName(String customerKatakanaFirstName) {
        this.customerKatakanaFirstName = customerKatakanaFirstName;
    }

    public String getCustomerKatakanaLastName() {
        return customerKatakanaLastName;
    }

    public void setCustomerKatakanaLastName(String customerKatakanaLastName) {
        this.customerKatakanaLastName = customerKatakanaLastName;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Byte getCustomerGender() {
        return customerGender;
    }

    public void setCustomerGender(Byte customerGender) {
        this.customerGender = customerGender;
    }

    public LocalDate getCustomerBirthday() {
        return customerBirthday;
    }

    public void setCustomerBirthday(LocalDate customerBirthday) {
        this.customerBirthday = customerBirthday;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getCustomerQrCode() {
        return customerQrCode;
    }

    public void setCustomerQrCode(String customerQrCode) {
        this.customerQrCode = customerQrCode;
    }
}
