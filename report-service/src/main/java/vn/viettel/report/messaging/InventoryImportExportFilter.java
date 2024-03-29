package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryImportExportFilter {

    private Long shopId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Long warehouseTypeId;
    private String productCodes;

    @Override
    public String toString() {
        return "InventoryImportExportFilter{" +
                "shopId=" + shopId +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", warehouseTypeId=" + warehouseTypeId +
                ", productCodes='" + productCodes + '\'' +
                '}';
    }
}
