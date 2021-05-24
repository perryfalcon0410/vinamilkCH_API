package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductReturnDTO {
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị")
    private String unit;
    @ApiModelProperty(notes = "Số lượng")
    private int quantity;
    @ApiModelProperty(notes = "Giá tiền mỗi đơn vị")
    private Float pricePerUnit;
    @ApiModelProperty(notes = "Tổng tiền")
    private Float totalPrice;
    @ApiModelProperty(notes = "Chiết khấu")
    private Float discount;
    @ApiModelProperty(notes = "Tiền trả lại")
    private Float paymentReturn;
}
