package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CustomerReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "FULL_NAME")
    private String customerName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "BIRTH_DAY")
    private LocalDateTime birthDay;
    @Column(name = "GENDER")
    private String gender;
    @Column(name = "ADDRESS")
    private String address;
}
