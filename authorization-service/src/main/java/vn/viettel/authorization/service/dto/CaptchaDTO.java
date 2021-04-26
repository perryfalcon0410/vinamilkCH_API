package vn.viettel.authorization.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaDTO {
    private ResponseMessage error;
    private String captcha;
}
