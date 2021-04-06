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
    private String onlineNumber; // if is receipt online
    private Integer onlineSubType;
    private Boolean usedRedInvoice;
    private String redInvoiceCompanyName;
    private String redInvoiceTaxCode;
    private String redInvoiceRemark;
    private Long orderOnlineId;

    private Long voucherId;
    private List<OrderDetailDTO> products;
    private List<ZmFreeItemDTO> freeItemList;
}
