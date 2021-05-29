package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalDTO {
    private Long totalQuantity = 0L;
    private Double totalPrice = 0.0;

    public TotalDTO addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }
    public TotalDTO addTotalPrice(Double totalPrice) {
        this.totalPrice += totalPrice;
        return this;
    }

}
