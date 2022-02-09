package vn.viettel.sale.messaging;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

@Getter
@Setter
@NoArgsConstructor
public class PrintDataRedInvoiceResponse {

    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNumber;
    @ApiModelProperty(notes = "Ngày in hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime datePrint;
    @ApiModelProperty(notes = "Số hóa đơn")
    private String saleOrderNumber;
    @ApiModelProperty(notes = "Mã cửa hàng")
    private String shopCode;
    @ApiModelProperty(notes = "Tên cửa hàng")
    private String shopName;
    @ApiModelProperty(notes = "Địa chỉ")
    private String shopAddress;
    @ApiModelProperty(notes = "Số điện thoại cửa hàng")
    private String shopTel;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Địa chỉ")
    private String customerAddress;
    @ApiModelProperty(notes = "Số điện thoại")
    private String customerPhone;
    @ApiModelProperty(notes = "Thành tiền")
    private Double amount;
    @ApiModelProperty(notes = "Thuế GTGT")
    private Double valueAddedTax;
    @ApiModelProperty(notes = "Tổng tiền phải trả số")
    private Double totalAmountNumber;
    @ApiModelProperty(notes = "Tổng tiền phải trả chữ")
    private String totalAmountString;
    @ApiModelProperty(notes = "Tổng số lượng các sản phẩm")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Mã cửa hàng cha")
    private String parentShopCode;
    @ApiModelProperty(notes = "Tên cửa hàng cha")
    private String parentShopName;
    @ApiModelProperty(notes = "Địa chỉ cửa hàng cha")
    private String parentShopAddress;
    @ApiModelProperty(notes = "Số điện thoại cửa hàng")
    private String parentShopTel;

}

