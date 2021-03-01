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
    @NotNull(responseMessage = ResponseMessage.USER_ID_MUST_BE_NOT_NULL)
    private Long userId;

    @NotNull(responseMessage = ResponseMessage.USER_OLD_PASSWORD_MUST_BE_NOT_NULL)
    private String oldPassword;

    @NotNull(responseMessage = ResponseMessage.USER_PASSWORD_MUST_BE_NOT_NULL)
    private String password;

}
