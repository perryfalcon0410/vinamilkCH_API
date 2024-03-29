package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class  OrderReturnRequest {
//    @NotNull(responseMessage = ResponseMessage.DATE_RETURN_MUST_NOT_BE_NULL)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
//    private LocalDateTime dateReturn;
    @ApiModelProperty(notes = "Số hóa đơn")
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_NUMBER_MUST_NOT_BE_NULL)
    private String orderNumber;
    @ApiModelProperty(notes = "Id lý do trả hảng")
    @NotNull(responseMessage = ResponseMessage.REASON_MUST_NOT_BE_NULL)
    private String reasonId;
    @ApiModelProperty(notes = "Lý do trả hàng")
    @NotNull(responseMessage = ResponseMessage.REASON_DESC_MUST_NOT_BE_NULL)
    @MaxTextLength(length = 4000, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String reasonDescription;
}
