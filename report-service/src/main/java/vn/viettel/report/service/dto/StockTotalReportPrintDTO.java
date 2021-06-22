package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.report.service.impl.StockTotalCatDTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockTotalReportPrintDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private LocalDate date;
    private String printDate;
    private StockTotalReportDTO totalInfo;
    private List<StockTotalCatDTO> dataByCat;
}
