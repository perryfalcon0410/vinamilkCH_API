package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO {

    @ApiModelProperty(notes = "Id hóa đơn")
    private Long saleOrderID;
    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerNumber;

    @ApiModelProperty(notes = "Loại đơn hàng")
    private String orderTypeName;

    @ApiModelProperty(notes = "Số đơn hàng")
    private String onlineNumber;

    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Tổng tiền trước chiết khấu")
    private double amount;
    @ApiModelProperty(notes = "Tổng tiền khuyến mãi ")
    private double totalPromotion;
    @ApiModelProperty(notes = "Doanh số tích lũy của đơn hàng")
    private double customerPurchase;
    @ApiModelProperty(notes = "Tổng tiền phải thanh toán(sau chiết khấu)")
    private double total;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Sử dụng hóa đơn đỏ: 1-Sử dụng ,0-Không sử dụng")
    private boolean usedRedInvoice;
    @ApiModelProperty(notes = "Ghi chú trên hóa đơn đỏ")
    private String redInvoiceRemark;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Ngày mua hàng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Nhân viên bán hàng")
    private String salesManName;
    @ApiModelProperty(notes = "Tên công ty trên hóa đơn đỏ")
    private String redInvoiceCompanyName;
    @ApiModelProperty(notes = "Mã số thuế trên hóa đơn đỏ")
    private String redInvoiceTaxCode;
    @ApiModelProperty(notes = "Địa chỉ hóa đơn đỏ")
    private String redInvoiceAddress;

    public double getAmount() {
        return (double)Math.round(amount);
    }

    public double getTotalPromotion() {
        return (double)Math.round(totalPromotion);
    }

    public double getCustomerPurchase() {
        return (double)Math.round(customerPurchase);
    }

    public double getTotal() {
        return (double)Math.round(total);
    }
}
