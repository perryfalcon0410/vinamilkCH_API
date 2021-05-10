package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReturnGoodsDTO {

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
    private Integer quantity;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "REFUNDS")
    private Float refunds;
    @Column(name = "TOTAL_REFUNDS")
    private Float totalRefunds;
    @Column(name = "PAY_DAY")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp payDay;
    @Column(name = "REASON_FOR_PAYMENT")
    private String reasonForPayment;
    @Column(name = "FEEDBACK")
    private String feedback;

}
