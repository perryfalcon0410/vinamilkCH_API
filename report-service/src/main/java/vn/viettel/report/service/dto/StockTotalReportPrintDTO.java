package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockTotalReportPrintDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private ShopDTO parentShop;
    private LocalDateTime date;
    private LocalDateTime printDate;
    private StockTotalReportDTO totalInfo;
    private List<StockTotalCatDTO> dataByCat;
}
