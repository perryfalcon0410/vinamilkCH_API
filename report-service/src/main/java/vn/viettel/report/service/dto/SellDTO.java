package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thông tin bán hàng")
@Entity
public class SellDTO {

    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Số hóa đơn")
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @ApiModelProperty(notes = "Số lượng hóa đơn")
    @Column(name = "SOME_BILLS")
    private Integer someBills;
    @ApiModelProperty(notes = "Ngày bán")
    @Column(name = "ORDER_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Tên khách hàng ")
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @ApiModelProperty(notes = "Mã khách hàng")
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @ApiModelProperty(notes = "Điện thoại")
    @Column(name = "NUMBER_PHONE")
    private String numberPhone;
    @ApiModelProperty(notes = "Ngành hàng")
    @Column(name = "INDUSTRY")
    private String industry;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "ĐVT")
    @Column(name = "UNIT")
    private String unit;
    @ApiModelProperty(notes = "Số lượng")
    @Column(name = "QUANTITY")
    private Integer quantity;
    @ApiModelProperty(notes = "Tổng số lượng")
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Giá bán")
    @Column(name = "PRICE")
    private Float price;
    @ApiModelProperty(notes = "Tổng cộng")
    @Column(name = "TOTAL")
    private Float total;
    @ApiModelProperty(notes = "Tổng tổng cộng")
    @Column(name = "TOTAL_TOTAL")
    private Float totalTotal;
    @ApiModelProperty(notes = "Khuyến mãi chưa thuế (trước thuế)")
    @Column(name = "PROMOTION_NOT_VAT")
    private Float promotionNotVAT;
    @ApiModelProperty(notes = "Tổng khuyến mãi chưa thuế")
    @Column(name = "TOTAL_PROMOTION_NOT_VAT")
    private Float totalPromotionNotVAT;
    @ApiModelProperty(notes = "Khuyến mãi sau thuế")
    @Column(name = "PROMOTION")
    private Float promotion;
    @ApiModelProperty(notes = "Tổng khuyến mãi sau thuế")
    @Column(name = "TOTAL_PROMOTION")
    private Float totalPromotion;
    @ApiModelProperty(notes = "Thanh toán")
    @Column(name = "PAY")
    private Float pay;
    @ApiModelProperty(notes = "Tổng thanh toán")
    @Column(name = "TOTAL_PAY")
    private Float totalPay;
    @ApiModelProperty(notes = "Mã nhân viên")
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @ApiModelProperty(notes = "Tên nhân viên")
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;
    @ApiModelProperty(notes = "Nhóm sản phẩm")
    @Column(name = "PRODUCT_GROUPS")
    private String productGroups;
    @ApiModelProperty(notes = "Số đơn online")
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @ApiModelProperty(notes = "Kênh bán")
    @Column(name = "SALES_CHANNEL")
    private String salesChannel;
    @ApiModelProperty(notes = "Ghi chú")
    @Column(name = "NOTE")
    private String note;
}
