package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin in hóa đơn bán hàng")
public class PrintSaleOrderDTO {
    @ApiModelProperty(notes = "Tên công ty")
    private String shopName;

    @ApiModelProperty(notes = "Điện thoại công ty")
    private String shopPhone;

    @ApiModelProperty(notes = "Địa chỉ công ty")
    private String shopAddress;

    @ApiModelProperty(notes = "Email công ty")
    private String shopEmail;

    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;

    @ApiModelProperty(notes = "Điện thoại khách hàng")
    private String customerPhone;

    @ApiModelProperty(notes = "Địa chỉ khách hàng")
    private String customerAddress;

    @ApiModelProperty(notes = "Phương thức vận chuyển")
    private String deliveryType;

    @ApiModelProperty(notes = "Doanh số tích lũy")
    private Double customerPurchase;

    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;

    @ApiModelProperty(notes = "Ngày hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    @ApiModelProperty(notes = "Người bán")
    private String userName;

    @ApiModelProperty(notes = "Thông tin chi tiết sản phẩm mua")
    private List<PrintProductSaleOrderDTO> products;

    @ApiModelProperty(notes = "Thông tin khuyến mãi tay tiền")
    private List<PrintZMZV19ZV20ZV23DTO> lstZM;

//    @ApiModelProperty(notes = "Thông tin khuyến mãi tiền zv19, zv20, zv23, zm")
//    private PrintZMZV19ZV20ZV23DTO zMZV19ZV20ZV23;

    @ApiModelProperty(notes = "Tổng bao gồm thuế")
    private Double amount;

    @ApiModelProperty(notes = "Tổng chưa gồm thuế")
    private Double amountNotVAT;

    @ApiModelProperty(notes = "Khuyến mãi chưa gồm thuế")
    private Double promotionAmountNotVat;

    @ApiModelProperty(notes = "Khuyến mãi gồm thuế")
    private Double promotionAmount;

    @ApiModelProperty(notes = "Giảm giá (dùng mã giảm giá)")
    private Double discountAmount;

    @ApiModelProperty(notes = "Tiền tích lũy sử dụng")
    private Double accumulatedAmount;

    @ApiModelProperty(notes = "Tiền tích lũy sử dụng")
    private Double voucherAmount;

    @ApiModelProperty(notes = "Thanh toán chưa vat")
    private Double totalNotVat;

    @ApiModelProperty(notes = "Thanh toán gồm vat")
    private Double total;

    @ApiModelProperty(notes = "Khách hàng đưa")
    private Double paymentAmount;

    @ApiModelProperty(notes = "Tiền thừa trả khách")
    private Double extraAmount;
}
