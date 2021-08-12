package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockTotalCatDTO {
    private String category;
    private Long totalQuantity;
    private Double totalAmount;
    private List<StockTotalReportDTO> data;
}
