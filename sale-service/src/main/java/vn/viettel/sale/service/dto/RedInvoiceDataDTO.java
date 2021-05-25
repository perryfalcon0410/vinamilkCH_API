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