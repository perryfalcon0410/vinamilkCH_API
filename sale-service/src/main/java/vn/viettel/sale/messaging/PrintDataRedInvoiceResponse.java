package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

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

}

