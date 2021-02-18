package vn.viettel.authorization.messaging.authorization;

import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.EmailAddress;
import vn.viettel.core.validation.annotation.NotNull;

public class UserRegisterRequest extends BaseRequest {

    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_BE_NOT_NULL)
    private String name;

    @NotNull(responseMessage = ResponseMessage.USER_EMAIL_MUST_BE_NOT_NULL)
    @EmailAddress(responseMessage = ResponseMessage.USER_EMAIL_FORMAT_NOT_CORRECT)
    private String email;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
