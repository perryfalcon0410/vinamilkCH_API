package vn.viettel.customer.messaging;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotEmpty;

@Getter
@Setter
public class CustomerBulkDeleteRequest extends BaseRequest {
    @NotEmpty(responseMessage = ResponseMessage.CUSTOMER_IDS_MUST_BE_NOT_NULL)
    private Long[] customerIds;
}
