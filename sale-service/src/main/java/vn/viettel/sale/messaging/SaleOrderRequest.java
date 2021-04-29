package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderRequest {
    @NotNull(responseMessage = ResponseMessage.SHOP_ID_MUST_NOT_BE_NULL)
    private Long shopId;
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_ID_MUST_BE_NOT_NULL)
    private Long customerId; // get ware_house_type_id from customer
    @NotNull(responseMessage = ResponseMessage.USER_ID_MUST_BE_NOT_NULL)
    private Long salemanId;
    @NotNull(responseMessage = ResponseMessage.WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll)
    private Long wareHouseTypeId;
    // money received from customer
    @NotNull(responseMessage = ResponseMessage.TOTAL_PAID_MUST_NOT_BE_NULL)
    private Float totalPaid;
    private String note;
    @NotNull(responseMessage = ResponseMessage.TYPE_NOT_BE_NULL)
    private Integer type; // sale or return
    private Long fromSaleOrderId; // if is return
    private Integer paymentType;
    private Integer deliveryType;
    // from AP_PARAM
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_TYPE_NOT_BE_NULL)
    private Integer orderType;

    //Online order
    private Long orderOnlineId;
    private String onlineNumber;
    private Integer onlineSubType;

    private Long voucherId;
    private List<ProductOrderRequest> products;
    private List<ZmFreeItemDTO> freeItemList;

}