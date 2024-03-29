package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Request tạo mới đơn hàng")
public class SaleOrderRequest {
    // order info
    @ApiModelProperty(notes = "Id khách hàng")
    @NotNull(responseMessage = ResponseMessage.CUSTOMER_ID_MUST_BE_NOT_NULL)
    private Long customerId; // get ware_house_type_id from customer

    @ApiModelProperty(notes = "Loại thanh toán")
    private Integer paymentType;

    @ApiModelProperty(notes = "Loại giao hàng")
    private Integer deliveryType;

    @ApiModelProperty(notes = "Loại đơn online/offline")
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_TYPE_NOT_BE_NULL)
    private Integer orderType;

    @MaxTextLength(length = 4000, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String note;

    //Online order
    @ApiModelProperty(notes = "Id đơn online được chọn (thanh toán đơn online)")
    private Long orderOnlineId;

    @ApiModelProperty(notes = "Số đơn online (Tạo tay đơn online hoặc đơn online được chọn")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.ONLINE_NUMBER_MAX_LENGTH_STRING)
    private String onlineNumber;

    @ApiModelProperty(notes = "Danh sách sản phẩm trong đơn hàng")
    private List<ProductOrderRequest> products;

    @ApiModelProperty(notes = "Danh sách khuyến mãi")
    private List<SalePromotionDTO> promotionInfo;

    // Thông tin thanh toán
    @NotNull(responseMessage = ResponseMessage.TOTAL_PAID_MUST_NOT_BE_NULL)
    private Double totalOrderAmount;

    @ApiModelProperty(notes = "Tiền khuyến mãi")
    private Double promotionAmount;

    @ApiModelProperty(notes = "Tiền tích lũy sử dụng")
    private Double accumulatedAmount;

    @ApiModelProperty(notes = "Tiền theo mã giảm giá")
    private Double discountAmount;

    @ApiModelProperty(notes = "Mã giảm giá")
    private String discountCode;

    @ApiModelProperty(notes = "Thông tin voucher")
    private List<OrderVoucherRequest> vouchers;

    @ApiModelProperty(notes = "Tiền cần thanh toán")
    private Double remainAmount;

    @ApiModelProperty(notes = "Tiền thanh toán")
    private Double paymentAmount;

    @ApiModelProperty(notes = "Tiền thừa trả khách")
    private Double extraAmount;

}
