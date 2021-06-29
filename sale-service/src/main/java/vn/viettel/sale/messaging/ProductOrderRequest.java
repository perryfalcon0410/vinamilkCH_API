package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.core.validation.annotation.NumberGreaterThanZero;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Danh sách sản phẩm mua")
public class ProductOrderRequest {

    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Sản phẩm combo")
    private boolean combo = false;
    @ApiModelProperty(notes = "Mã phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Số lượng")
    @NumberGreaterThanZero(responseMessage = ResponseMessage.NUMBER_GREATER_THAN_ZERO)
    private Integer quantity;

}
