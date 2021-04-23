package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransRequest extends BaseRequest {
    private Long id;
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Long customerId;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Float totalAmount;
    private List<ExchangeTransDetailRequest> lstExchangeDetail;
}
