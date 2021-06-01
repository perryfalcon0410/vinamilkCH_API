package vn.viettel.sale.service.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceDataDTO extends BaseDTO {
    @ApiModelProperty(notes = "ID đơn hàng")
    private Long saleOrderId;
    @ApiModelProperty(notes = "ID sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;
    @ApiModelProperty(notes = "ĐVT1")
    private String uom1;
    @ApiModelProperty(notes = "ĐVT2")
    private String uom2;
    @ApiModelProperty(notes = "Số lượng")
    private Float quantity;
    @ApiModelProperty(notes = "Giá trước thuế")
    private Double price;
    @ApiModelProperty(notes = "Giá sau thuế")
    private Double priceNotVat;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Double amount;
    @ApiModelProperty(notes = "Thành tiền sau thế")
    private Double amountNotVat;
    @ApiModelProperty(notes = "Thuế")
    private Double vat;
    @ApiModelProperty(notes = "Thuế giá trị gia tăng")
    private Double valueAddedTax;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
}