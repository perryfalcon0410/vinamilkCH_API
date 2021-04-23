package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTranDetailRequest extends BaseRequest {

    private Long comboProductId;

    private Integer quantity;

    private Float price;

}
