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
    private Long totalQuantity;
    private Double totalTotal;
    private Double totalPromotionNotVAT;
    private Double totalPromotion;
    private Double totalPay;
}
