package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportExportInventoryTotalDTO {

    private Integer beginningQuantity;

    private Float beginningAmount;

    private Integer importTotalQuantity;

    private Integer importQuantity;

    private Float importAmount;

    private Integer importAdjustmentQuantity;

    private Float importAdjustmentAmount;

    private Integer exportTotalQuantity;

    private Integer exportSalesQuantity;

    private Float exportSalesAmount;

    private Integer exportPromotionQuantity;

    private Float exportPromotionAmount;

    private Integer exportAdjustmentQuantity;

    private Float exportAdjustmentAmount;

    private Integer exportExchangeQuantity;

    private Float exportExchangeAmount;

    private Integer endingQuantity;

    private Float endingAmount;
}
