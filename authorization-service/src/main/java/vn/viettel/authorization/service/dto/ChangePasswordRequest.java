package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_NOT_BE_NULL)
    private String username;

    @NotNull(responseMessage = ResponseMessage.USER_OLD_PASSWORD_MUST_BE_NOT_NULL)
    private String oldPassword;

    @NotNull(responseMessage = ResponseMessage.NEW_PASSWORD_MUST_BE_NOT_NULL)
    private String newPassword;

    @NotNull(responseMessage = ResponseMessage.CONFIRM_PASSWORD_MUST_BE_NOT_NULL)
    private String confirmPassword;

}
