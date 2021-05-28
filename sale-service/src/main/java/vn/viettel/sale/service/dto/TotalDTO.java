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
    private Float totalPrice = 0F;

    public TotalDTO addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }
    public TotalDTO addTotalPrice(Float totalPrice) {
        this.totalPrice += totalPrice;
        return this;
    }

}
