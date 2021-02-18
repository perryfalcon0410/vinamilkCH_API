package vn.viettel.authorization.messaging.customer;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class CustomerValidActivationCodeRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_TOKEN_MUST_BE_NOT_NULL)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
