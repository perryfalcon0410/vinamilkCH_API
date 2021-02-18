package vn.viettel.authorization.messaging.authorization;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class UserForgotPasswordRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
