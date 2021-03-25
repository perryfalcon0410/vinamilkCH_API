package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetOrderDetailRequest {
    private long soId;
    private long payId;
    private long disId;
    private long cusId;
}
