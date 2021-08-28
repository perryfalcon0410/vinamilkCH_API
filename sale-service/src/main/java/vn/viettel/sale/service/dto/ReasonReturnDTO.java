package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin lý do trả")
public class ReasonReturnDTO {
    @ApiModelProperty(notes = "Mã apparam")
    private String apCode;
    @ApiModelProperty(notes = "Tên apparam")
    private String apName;
    @ApiModelProperty(notes = "Giá trị apparam")
    private String value;
}
