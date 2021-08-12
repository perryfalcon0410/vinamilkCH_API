package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.core.validation.annotation.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class  OrderReturnRequest {
//    @NotNull(responseMessage = ResponseMessage.DATE_RETURN_MUST_NOT_BE_NULL)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
//    private LocalDateTime dateReturn;
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_NUMBER_MUST_NOT_BE_NULL)
    private String orderNumber;
    @NotNull(responseMessage = ResponseMessage.REASON_MUST_NOT_BE_NULL)
    private String reasonId;
    @NotNull(responseMessage = ResponseMessage.REASON_DESC_MUST_NOT_BE_NULL)
    @MaxTextLength(length = 4000, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String reasonDescription;
}
