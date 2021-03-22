package vn.viettel.sale.service.dto;

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
    private Long cusId;
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_TYPE_MUST_NOT_BE_NULL)
    private Long saleOrderTypeId;
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private Long shopId;
    private Long receiptOnlineId;
    private Long redInvoiceId;
    @NotNull(responseMessage = ResponseMessage.DELIVERY_TYPE_MUST_BE_NOT_NULL)
    private boolean deliveryType;
    private boolean redReceiptExport;
    private String note;

    private Long wareHouseId;
    private int customerRealPay;
    private String redReceiptNote;
    private List<OrderDetailDTO> products;
}
