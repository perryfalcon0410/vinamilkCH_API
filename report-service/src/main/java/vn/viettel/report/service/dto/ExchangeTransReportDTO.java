package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ExchangeTransReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TRANS_DATE")
    private Long transDate;
    @Column(name = "TRANS_NUMBER")
    private Long transNumber;
    @Column(name = "CUSTOMER_CODE")
    private Long customerCode;
    @Column(name = "CUSTOMER_NAME")
    private Long customerName;
    @Column(name = "ADDRESS")
    private Long address;
    @Column(name = "PRODUCT_CODE")
    private Long productCode;
    @Column(name = "PRODUCT_NAME")
    private Long productName;
    @Column(name = "QUANTITY")
    private Long quantity;
    @Column(name = "AMOUNT")
    private Long amount;
    @Column(name = "CATEGORY_NAME")
    private Long categoryName;
    @Column(name = "PHONE")
    private Long phone;
}
