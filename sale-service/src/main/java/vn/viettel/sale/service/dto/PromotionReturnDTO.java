package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionReturnDTO {
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị")
    private String unit;
    @ApiModelProperty(notes = "Số lượng")
    private int quantity;
    @ApiModelProperty(notes = "Giá mỗi đơn vị")
    private float pricePerUnit;
    @ApiModelProperty(notes = "Tiền phải trả")
    private float paymentReturn;
}
