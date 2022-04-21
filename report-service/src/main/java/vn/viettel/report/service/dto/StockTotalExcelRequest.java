package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockTotalExcelRequest {
    private List<StockTotalReportDTO> stockTotals;
    private StockTotalInfoDTO totalInfo;
}
