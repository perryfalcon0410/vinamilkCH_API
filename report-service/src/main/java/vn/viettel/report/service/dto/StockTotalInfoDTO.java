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
    private Long totalQuantity;
    private Long totalPackageQuantity;
    private Long totalUnitQuantity;
    private Double totalAmount;
}
