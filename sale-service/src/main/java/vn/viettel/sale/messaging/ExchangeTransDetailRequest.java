package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.core.validation.annotation.NumberGreaterThanZero;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDetailRequest extends BaseRequest {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Double price;
    @NotNull(responseMessage = ResponseMessage.TYPE_NOT_BE_NULL)
    private Integer type;
    @NumberGreaterThanZero(responseMessage = ResponseMessage.NUMBER_GREATER_THAN_ZERO)
    @NotNull(responseMessage = ResponseMessage.QUANTITY_CAN_NOT_BE_NULL)
    private Integer quantity;
    private Double totalPrice;
}
