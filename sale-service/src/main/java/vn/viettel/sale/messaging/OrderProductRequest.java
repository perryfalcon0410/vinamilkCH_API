package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequest extends BaseRequest {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;

}
