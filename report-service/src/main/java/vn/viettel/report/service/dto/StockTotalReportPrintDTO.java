package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockTotalReportPrintDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private LocalDateTime date;
    private LocalDateTime printDate;
    private StockTotalReportDTO totalInfo;
    private List<StockTotalCatDTO> dataByCat;
}
