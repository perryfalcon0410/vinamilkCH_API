package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin truyền vào để đổi mật khẩu")
public class ChangePasswordRequest {
    @ApiModelProperty(notes = "Tên đăng nhập")
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_NOT_BE_NULL)
    private String username;

    @ApiModelProperty(notes = "Mật khẩu cũ")
    @NotNull(responseMessage = ResponseMessage.USER_OLD_PASSWORD_MUST_BE_NOT_NULL)
    private String oldPassword;

    @ApiModelProperty(notes = "Mật khẩu mới")
    @NotNull(responseMessage = ResponseMessage.NEW_PASSWORD_MUST_BE_NOT_NULL)
    private String newPassword;

    @ApiModelProperty(notes = "Xác nhận mật khẩu mới")
    @NotNull(responseMessage = ResponseMessage.CONFIRM_PASSWORD_MUST_BE_NOT_NULL)
    private String confirmPassword;

}
