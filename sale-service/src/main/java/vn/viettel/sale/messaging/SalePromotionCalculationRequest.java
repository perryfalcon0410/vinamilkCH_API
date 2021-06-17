package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalePromotionCalculationRequest {

    @NotNull(responseMessage = ResponseMessage.CUSTOMER_ID_MUST_BE_NOT_NULL)
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;

    @NotNull(responseMessage = ResponseMessage.ORDER_TYPE_NOT_NULL)
    @ApiModelProperty(notes = "Loại mua hàng")
    private Integer orderType;

    @ApiModelProperty(notes = "Tiền cần thanh toán")
    @NotNull(responseMessage = ResponseMessage.TOTAL_PAID_MUST_NOT_BE_NULL)
    private Double totalOrderAmount;

    @ApiModelProperty(notes = "Tiền tích lũy")
    private Double accumulatedAmount;

    @ApiModelProperty(notes = "Tiền voucher")
    private Double voucherAmount;

    @ApiModelProperty(notes = "Tiền giảm giá")
    private Double discountAmount;

    @ApiModelProperty(notes = "Danh sách khuyến mãi tiền và % giảm giá")
    private List<SalePromotionCalItemRequest> promotionInfo;

    @ApiModelProperty(notes = "Thông tin mua hàng")
    private OrderPromotionRequest orderRequest;

}
