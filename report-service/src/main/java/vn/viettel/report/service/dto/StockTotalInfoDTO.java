package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTotalInfoDTO {
    private Integer totalQuantity;
    private Integer totalPackageQuantity;
    private Integer totalUnitQuantity;
    private Float totalAmount;
}
