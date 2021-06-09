package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportGoodFilter {
    private Long shopId;
    private LocalDate fromExportDate;
    private LocalDate toExportDate;
    private LocalDate fromOrderDate;
    private LocalDate toOrderDate;
    private String lstProduct;
    private String lstExportType;
    private String searchKeywords;
}
