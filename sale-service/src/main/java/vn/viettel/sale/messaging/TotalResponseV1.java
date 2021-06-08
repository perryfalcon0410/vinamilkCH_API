package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalResponseV1 {
    private Integer totalQuantity = 0;
    private Integer countProduct = 0;
    private Double totalPrice = 0D;
    private Double totalPriceNotVat = 0D;

}
