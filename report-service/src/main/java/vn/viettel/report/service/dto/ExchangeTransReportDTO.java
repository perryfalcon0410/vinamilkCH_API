package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ExchangeTransReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "TRANS_NUMBER")
    private String transNumber;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "PRODCUT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "CATEGORY_NAME")
    private String categoryName;
    @Column(name = "PHONE")
    private String phone;
}
