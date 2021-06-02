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
@AllArgsConstructor
@NoArgsConstructor
public class InventoryImportExportFilter {

    private Long shopId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String productCodes;

}
