package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReturnGoodsReportDTO {

    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "RETURN_CODE")
    private String returnCode;
    @Column(name = "RECIEPT")
    private String reciept;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "INDUSTRY")
    private String industry;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "QUANTITY")
    private Integer quatity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "REFUNDS")
    private Float refunds;
    @Column(name = "PAY_DAY")
    private Date payDay;
    @Column(name = "REASON_FOR_PAYMENT")
    private String reasonForPayment;
    @Column(name = "FEEDBACK")
    private String feedback;

}
