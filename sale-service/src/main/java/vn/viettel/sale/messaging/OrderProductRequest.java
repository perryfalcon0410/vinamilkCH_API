package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequest extends BaseRequest {

    private Long productId;

    private Integer quantity;

}