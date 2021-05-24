package vn.viettel.report.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.report.service.dto.PrintGoodDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrintGoodFilter {
    @ApiModelProperty(notes = "Danh sách xuất điều chỉnh")
    private List<PrintGoodDTO> lstAdjust;
    @ApiModelProperty(notes = "Danh sách xuất trả Po")
    private List<PrintGoodDTO> lstPo;
    @ApiModelProperty(notes = "Danh sách xuất vay mượn")
    private List<PrintGoodDTO> lstStock;
}
