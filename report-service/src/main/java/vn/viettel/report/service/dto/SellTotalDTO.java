package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellTotalDTO {
    private Integer someBills;
    private Integer totalQuantity;
    private Float totalTotal;
    private Float totalPromotion;
    private Float totalPay;
}
