package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DVKHExcelDTO {
    @ApiModelProperty(notes = "Dữ liệu sheet HD")
    private List<HDDTO> lstHD;
    @ApiModelProperty(notes = "Dữ liệu sheet CT")
    private List<CTDTO> lstCT;
}
