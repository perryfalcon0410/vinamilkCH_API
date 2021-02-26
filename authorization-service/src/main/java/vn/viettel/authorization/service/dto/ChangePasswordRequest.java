package vn.viettel.authorization.service.dto;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

public class ChangePasswordRequest {
    @NotNull(responseMessage = ResponseMessage.USER_ID_MUST_BE_NOT_NULL)
    private Long userId;

    @NotNull(responseMessage = ResponseMessage.USER_OLD_PASSWORD_MUST_BE_NOT_NULL)
    private String oldPassword;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
