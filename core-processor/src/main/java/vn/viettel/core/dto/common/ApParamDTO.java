package vn.viettel.core.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ApParamDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã tham số")
    private String apParamCode;
    @ApiModelProperty(notes = "Tên tham số")
    private String apParamName;
    @ApiModelProperty(notes = "Giá trị tham số")
    private String value;
    @ApiModelProperty(notes = "Mô tả")
    private String description;
    @ApiModelProperty(notes = "Loại tham số")
    private String type;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
}
