package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ChangePriceTotalDTO {
    private Integer totalQuantity;
    private Float totalPriceInput;
    private Float totalPriceOutput;
}
