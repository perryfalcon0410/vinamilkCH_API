package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Mã captcha")
public class CaptchaDTO {
    @ApiModelProperty(notes = "Câu thông báo lỗi")
    private ResponseMessage error;
    @ApiModelProperty(notes = "Mã captcha")
    private String captcha;
}
