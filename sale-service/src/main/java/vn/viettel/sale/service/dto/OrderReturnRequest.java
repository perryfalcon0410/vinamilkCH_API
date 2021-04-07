package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnRequest {
    @NotNull(responseMessage = ResponseMessage.DATE_RETURN_MUST_NOT_BE_NULL)
    private String orderReturnNumber;
    @NotNull(responseMessage = ResponseMessage.DATE_RETURN_MUST_NOT_BE_NULL)
    private Timestamp dateReturn;
    @NotNull(responseMessage = ResponseMessage.SALE_ORDER_NUMBER_MUST_NOT_BE_NULL)
    private String orderNumber;
    @NotNull(responseMessage = ResponseMessage.REASON_MUST_NOT_BE_NULL)
    private long reasonId;
    private String reasonDescription;
    @NotNull(responseMessage = ResponseMessage.CREATE_USER_MUST_NOT_BE_NULL)
    private String createUser;
}
