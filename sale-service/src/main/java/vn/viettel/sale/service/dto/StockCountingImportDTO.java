package vn.viettel.sale.service.dto;

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
    private List<StockCountingDetailDTO> importSuccess;
    private List<StockCountingExcel> importFails;
}
