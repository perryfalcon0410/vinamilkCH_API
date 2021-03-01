package vn.viettel.core.dto.salon;

import java.time.LocalDate;

public class SalonConfirmationMemberDetailDTO {
    private Long customerId;

    private String firstName;

    private String lastName;

    private String katakanaLastName;

    private String katakanaFirstName;

    private LocalDate birthday;

    private String tel;

    private Double point;

    public SalonConfirmationMemberDetailDTO() {
    }

    public SalonConfirmationMemberDetailDTO(Long customerId, String firstName, String lastName, String katakanaLastName, String katakanaFirstName, LocalDate birthday, String tel, Double point) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.katakanaLastName = katakanaLastName;
        this.katakanaFirstName = katakanaFirstName;
        this.birthday = birthday;
        this.tel = tel;
        this.point = point;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }
}
