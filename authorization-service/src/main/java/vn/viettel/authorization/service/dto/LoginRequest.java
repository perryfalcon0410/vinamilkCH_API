package vn.viettel.authorization.service.dto;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class LoginRequest {
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_NOT_BE_NULL)
    private String username;
    @NotNull(responseMessage = ResponseMessage.PASSWORD_MUST_NOT_BE_NULL)
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
