package vn.viettel.sale.service.dto;

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
public class StockCountingImportDTO {
    @ApiModelProperty(notes = "Danh sách sảm phẩm import thành công")
    private List<StockCountingDetailDTO> importSuccess;
    @ApiModelProperty(notes = "Danh sách sảm phẩm import lỗi")
    private List<StockCountingExcel> importFails;
}
