package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePriceTotalDTO {
    private String poNumber;
    private Integer totalQuantity;
    private Float totalPriceInput;
    private Float totalPriceOutput;

    public ChangePriceTotalDTO(String poNumber) {
        this.poNumber = poNumber;
    }
}
