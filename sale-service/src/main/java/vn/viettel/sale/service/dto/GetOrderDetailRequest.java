package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class GetOrderDetailRequest {
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_ID_MUST_NOT_BE_NULL)
    private long saleOrderId;
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_NUMBER_MUST_NOT_BE_NULL)
    private String orderNumber;
}
