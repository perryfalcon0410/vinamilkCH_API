package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin truyền vào để đăng nhập")
public class LoginRequest extends BaseDTO {
    @ApiModelProperty(notes = "Tên đăng nhập")
    @NotNull(responseMessage = ResponseMessage.USER_NAME_MUST_NOT_BE_NULL)
    private String username;
    @ApiModelProperty(notes = "Mật khẩu")
    @NotNull(responseMessage = ResponseMessage.PASSWORD_MUST_NOT_BE_NULL)
    private String password;
    @ApiModelProperty(notes = "ID vai trò")
    private Long roleId;
    @ApiModelProperty(notes = "ID cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Mã captcha(nếu đc yêu cầu nhập)")
    private String captchaCode;

    @ApiModelProperty(notes = "Địa chỉ mac")
    private String macAdrress;
    @ApiModelProperty(notes = "Computer name")
    private String computerName;
}
