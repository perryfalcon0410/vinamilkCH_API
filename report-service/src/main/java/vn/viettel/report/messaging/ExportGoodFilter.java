package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportGoodFilter {
    private Long shopId;
    private Date fromExportDate;
    private Date toExportDate;
    private Date fromOrderDate;
    private Date toOrderDate;
    private String lstProduct;
    private String lstExportType;
    private String searchKeywords;
}
