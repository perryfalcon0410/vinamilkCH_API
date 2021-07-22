package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InOutAdjustmentTotalDTO {
    private Integer totalQuantity;
    private Float totalPrice;
}
