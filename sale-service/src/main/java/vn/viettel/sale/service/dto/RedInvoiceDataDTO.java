package vn.viettel.sale.service.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceDataDTO extends BaseDTO {
    @ApiModelProperty(notes = "ID shop ")
    private Long shopId;
    @ApiModelProperty(notes = "ID khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "ID đơn hàng")
    private Long saleOrderId;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNumber;
    @ApiModelProperty(notes = "Ngày tạo hóa đơn")
    private Date printDate;
    @ApiModelProperty(notes = "Tên đơn vị(VAT)")
    private String officeWorking;
    @ApiModelProperty(notes = "Địa chỉ đơn vị(VAT)")
    private String officeAddress;
    @ApiModelProperty(notes = "Mã số thuế")
    private String taxCode;
    @ApiModelProperty(notes = "Tên người in hóa đơn")
    private String createUser;
    @ApiModelProperty(notes = "Loại thanh toán")
    private Integer paymentType;
    @ApiModelProperty(notes = "ID sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "ĐVT1")
    private String uom1;
    @ApiModelProperty(notes = "ĐVT2")
    private String uom2;
    @ApiModelProperty(notes = "Số lượng")
    private Float quantity;
    @ApiModelProperty(notes = "Giá trước thuế")
    private Float price;
    @ApiModelProperty(notes = "Giá sau thuế")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Float amount;
    @ApiModelProperty(notes = "Thành tiền sau thế")
    private Float amountNotVat;
    @ApiModelProperty(notes = "Thuế")
    private Float vat;
    @ApiModelProperty(notes = "Thuế giá trị gia tăng")
    private Float valueAddedTax;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
}