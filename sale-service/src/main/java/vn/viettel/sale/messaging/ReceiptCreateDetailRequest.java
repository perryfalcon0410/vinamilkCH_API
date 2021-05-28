package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NumberGreaterThanZero;
import vn.viettel.core.validation.annotation.NotNull;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateDetailRequest extends BaseRequest {
    @ApiModelProperty(notes = "Id chi tiết đơn nhập hàng")
    private Long id;
    @ApiModelProperty(notes = "Id đơn nhập hàng")
    private Long transId;
    @ApiModelProperty(notes = "Ngày nhập")
    private Date transDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id sản phẩm")
    @NotNull(responseMessage = ResponseMessage.PLEASE_IMPORT_PRODUCTS)
    private Long productId;
    @ApiModelProperty(notes = "Số lượng")
    @NumberGreaterThanZero(responseMessage = ResponseMessage.NUMBER_GREATER_THAN_ZERO)
    @NotNull(responseMessage = ResponseMessage.QUANTITY_CAN_NOT_BE_NULL)
    private Integer quantity;
    @ApiModelProperty(notes = "Giá")
    private Float price;
    @ApiModelProperty(notes = "Giá trước thuế")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Thành tiền")
    private Float amount;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Float amountNotVat;
}
