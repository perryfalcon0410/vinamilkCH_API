package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO {
    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerNumber;
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
    private Date orderDate;
    @ApiModelProperty(notes = "Nhân viên bán hàng")
    private String salesManName;
    @ApiModelProperty(notes = "Tên công ty trên hóa đơn đỏ")
    private String redInvoiceCompanyName;
    @ApiModelProperty(notes = "Mã số thuế trên hóa đơn đỏ")
    private String redInvoiceTaxCode;
    @ApiModelProperty(notes = "Địa chỉ hóa đơn đỏ")
    private String radInvoiceAddress;
}
