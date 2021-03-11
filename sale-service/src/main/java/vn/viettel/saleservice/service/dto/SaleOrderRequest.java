package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderRequest {
    @NotNull(responseMessage = ResponseMessage.USER_ID_MUST_BE_NOT_NULL)
    private long cusId;
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private long receiptTypeId;
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private long receiptOnlineId;
    @NotNull(responseMessage = ResponseMessage.WAREHOUSE_ID_MUST_NOT_BE_NUll)
    private long wareHouseId;
    @NotNull(responseMessage = ResponseMessage.DELIVERY_TYPE_MUST_BE_NOT_NULL)
    private boolean deliveryType;
    @NotNull(responseMessage = ResponseMessage.PAYMENT_METHOD_MUST_BE_NOT_NULL)
    private int paymentMethod;
    private boolean redReceiptExport;
    private List<SaleOrderDetailDto> products;
}
