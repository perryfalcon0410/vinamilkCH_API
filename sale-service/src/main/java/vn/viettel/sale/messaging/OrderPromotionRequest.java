package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPromotionRequest {

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_ID_MUST_BE_NOT_NULL)
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;

    @NotNull(responseMessage = ResponseMessage.ORDER_TYPE_NOT_NULL)
    @ApiModelProperty(notes = "Loại mua hàng")
    private Integer orderType;

    @ApiModelProperty(notes = "Danh sách sản phẩm mua")
    private List<@Valid ProductOrderRequest> products;

}
