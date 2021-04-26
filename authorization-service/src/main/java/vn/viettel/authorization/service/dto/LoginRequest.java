package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest extends BaseDTO {
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_NOT_BE_NULL)
    private String username;
    @NotNull(responseMessage = ResponseMessage.PASSWORD_MUST_NOT_BE_NULL)
    private String password;
    private Long roleId;
    private Long shopId;
    private String captchaCode;
}
