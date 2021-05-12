package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Thông tin hàng trả lại")
@Entity
public class ReturnGoodsDTO {

    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Mã trả hàng")
    @Column(name = "RETURN_CODE")
    private String returnCode;
    @ApiModelProperty(notes = "Hóa đơn mua hàng")
    @Column(name = "RECIEPT")
    private String reciept;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "FULL_NAME")
    private String fullName;
    @ApiModelProperty(notes = "Ngành hàng")
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
    @ApiModelProperty(notes = "Tiền trả lại")
    @Column(name = "REFUNDS")
    private Float refunds;
    @Column(name = "TOTAL_REFUNDS")
    private Float totalRefunds;
    @ApiModelProperty(notes = "Ngày trả")
    @Column(name = "PAY_DAY")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp payDay;
    @ApiModelProperty(notes = "Lý do trả")
    @Column(name = "REASON_FOR_PAYMENT")
    private String reasonForPayment;
    @ApiModelProperty(notes = "Thông tin phản hồi")
    @Column(name = "FEEDBACK")
    private String feedback;

}
