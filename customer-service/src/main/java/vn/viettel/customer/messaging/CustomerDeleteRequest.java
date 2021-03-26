package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotNull;

@Setter
@Getter
public class CustomerDeleteRequest extends BaseRequest {
    @NotNull(responseMessage = ResponseMessage.ID_MUST_NOT_BE_NULL)
    private Long id;

}
