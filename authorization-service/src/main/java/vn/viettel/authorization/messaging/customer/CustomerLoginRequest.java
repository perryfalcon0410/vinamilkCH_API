package vn.viettel.authorization.messaging.customer;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class CustomerLoginRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    private String email;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
