package vn.viettel.core.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách sản phẩm mua")
public class PromotionProductRequest {

    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Số lượng mua")
    private Integer quantity;
}
