package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

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
    @ApiModelProperty(notes = "Mã khách hàng")
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @ApiModelProperty(notes = "Tên khách hàng")
    @Column(name = "FULL_NAME")
    private String fullName;
    @ApiModelProperty(notes = "Ngành hàng")
    @Column(name = "INDUSTRY")
    private String industry;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị tính")
    @Column(name = "UNIT")
    private String unit;
    @ApiModelProperty(notes = "Số lượng")
    @Column(name = "QUANTITY")
    private Integer quantity;
    @ApiModelProperty(notes = "Tổng số lượng")
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Giá")
    @Column(name = "PRICE")
    private Float price;
    @ApiModelProperty(notes = "Thành tiền")
    @Column(name = "AMOUNT")
    private Float amount;
    @ApiModelProperty(notes = "Tổng thành tiền")
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @ApiModelProperty(notes = "Tiền trả lại")
    @Column(name = "REFUNDS")
    private Float refunds;
    @ApiModelProperty(notes = "Tổng tiền trả lại")
    @Column(name = "TOTAL_REFUNDS")
    private Float totalRefunds;
    @ApiModelProperty(notes = "Ngày trả")
    @Column(name = "PAY_DAY")
    private LocalDateTime payDay;
    @ApiModelProperty(notes = "Lý do trả")
    @Column(name = "REASON_FOR_PAYMENT")
    private String reasonForPayment;
    @ApiModelProperty(notes = "Thông tin phản hồi")
    @Column(name = "FEEDBACK")
    private String feedback;
    @ApiModelProperty(notes = "id ngành hàng")
    @Column(name = "INDUSTRYID")
    private Long industryId;
    @Column(name = "RETURN_ID")
    private Long returnId;
}
