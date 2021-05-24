package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Request tạo mới đơn hàng")
public class SaleOrderRequest {
    @ApiModelProperty(notes = "Id cửa hàng")
    @NotNull(responseMessage = ResponseMessage.SHOP_ID_MUST_NOT_BE_NULL)
    private Long shopId;
    @ApiModelProperty(notes = "Id khách hàng")
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
    // from AP_PARAM
    @ApiModelProperty(notes = "Loại thanh toán")
    private Integer paymentType;
    @ApiModelProperty(notes = "Loại giao hàng")
    private Integer deliveryType;
    @ApiModelProperty(notes = "Loại đơn online/offline")
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_TYPE_NOT_BE_NULL)
    private Integer orderType;
    @ApiModelProperty(notes = "Tiền tích lũy sử dụng")
    private Float accumulatedMoney;
    @ApiModelProperty(notes = "Mã giảm giá")
    private String discountCode;

    //Online order
    @ApiModelProperty(notes = "Id đơn online được chọn (thanh toán đơn online)")
    private Long orderOnlineId;
    @ApiModelProperty(notes = "Số đơn online (Tạo tay đơn online hoặc đơn online được chọn")
    private String onlineNumber;
    @ApiModelProperty(notes = "Đối với đơn online: 1 tạo tay, 2 đơn online, 3 đơn online có chỉnh sửa")
    private Integer onlineSubType;

    @ApiModelProperty(notes = "Id voucher")
    private Long voucherId;
    @ApiModelProperty(notes = "Danh sách sản phẩm trong đơn hàng")
    private List<ProductOrderRequest> products;
    @ApiModelProperty(notes = "Danh sách sản phẩm khuyến mãi")
    private List<ZmFreeItemDTO> freeItemList;

}
