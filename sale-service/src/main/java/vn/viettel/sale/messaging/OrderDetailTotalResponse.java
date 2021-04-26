package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailTotalResponse {
    private int totalQuantity;
    private float totalAmount;
    private float totalDiscount;
    private float totalPayment;
}
