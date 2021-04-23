package vn.viettel.core.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ApParamDTO extends BaseDTO {
    private String apParamCode;
    private String apParamName;
    private String value;
    private String description;
    private String type;
    private Integer status;
}
