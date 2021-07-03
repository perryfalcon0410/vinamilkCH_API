package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrintShopImportFilterDTO {
    @ApiModelProperty(notes = "Danh sách xuất trả Po")
    private List<orderImportDTO> lstPo;
    @ApiModelProperty(notes = "Danh sách xuất vay mượn")
    private List<orderImportDTO> lstBorrow;
    @ApiModelProperty(notes = "Danh sách xuất điều chỉnh")
    private List<orderImportDTO> lstAdjust;
}
