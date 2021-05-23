package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Danh sách sản phẩm mua")
public class ProductOrderRequest {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    private Float zmPromotion;
}
