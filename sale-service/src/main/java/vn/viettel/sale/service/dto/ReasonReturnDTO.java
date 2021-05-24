package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import liquibase.pro.packaged.S;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReasonReturnDTO {
    @ApiModelProperty(notes = "Mã apparam")
    private String apCode;
    @ApiModelProperty(notes = "Tên apparam")
    private String apName;
    @ApiModelProperty(notes = "Giá trị apparam")
    private String value;
}
