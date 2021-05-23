package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderRequest {
    private Long productId;
    private String productCode;
    private Integer quantity;
    private Float zmPromotion;
}
