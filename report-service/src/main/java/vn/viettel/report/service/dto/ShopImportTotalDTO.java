package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopImportTotalDTO {
    private Integer totalQuantity;
    private Integer totalWholeSale;
    private Integer totalRetail;
    private Float totalAmount;
    private Float total;
}
