package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalResponse {
    private Integer totalQuantity = 0;
    private Double totalPrice = 0D;

    public TotalResponse addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }
    public TotalResponse addTotalPrice(Double totalPrice) {
        this.totalPrice += totalPrice;
        return this;
    }



}
