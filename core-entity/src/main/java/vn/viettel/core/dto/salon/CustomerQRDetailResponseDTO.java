package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;

public class CustomerQRDetailResponseDTO {
    private Long id;

    private Long salonId;

    private String firstName;

    private String lastName;

    private String katakanaLastName;

    private String katakanaFirstName;

    private LocalDateTime birthday;

    private String tel;

    public CustomerQRDetailResponseDTO() {
    }

    public CustomerQRDetailResponseDTO(Long id, Long salonId, String firstName, String lastName, String katakanaLastName, String katakanaFirstName, LocalDateTime birthday, String tel) {
        this.id = id;
        this.salonId = salonId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.katakanaLastName = katakanaLastName;
        this.katakanaFirstName = katakanaFirstName;
        this.birthday = birthday;
        this.tel = tel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
