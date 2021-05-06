package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderTotalResponse {
    private Float totalAmount = 0F;
    private Float allTotal = 0F;

    public SaleOrderTotalResponse addTotalAmount(Float totalAmount) {
        this.totalAmount += totalAmount;
        return this;
    }
    public SaleOrderTotalResponse addAllTotal(Float allTotal) {
        this.allTotal += allTotal;
        return this;
    }
}
