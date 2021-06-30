package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTotalDTO {
    private Integer totalQuantity;
    private Double totalAmount;

    public ExchangeTotalDTO (Long totalQuantity, Double totalAmount){
        this.totalQuantity = totalQuantity.intValue();
        this.totalAmount = totalAmount;
    }
}
