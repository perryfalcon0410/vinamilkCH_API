package vn.viettel.authorization.messaging.customer;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class CustomerChangePasswordRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_OLD_PASSWORD_MUST_BE_NOT_NULL)
    private String oldPassword;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
