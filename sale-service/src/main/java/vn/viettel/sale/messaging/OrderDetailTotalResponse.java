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
    private Integer totalQuantity;
    private Double totalAmount;
    private Double totalDiscount;
    private Double totalPayment;
}
