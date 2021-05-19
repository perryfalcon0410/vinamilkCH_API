package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDetailRequest extends BaseRequest {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Float price;
    private Integer quantity;
    private Float totalPrice;
}
