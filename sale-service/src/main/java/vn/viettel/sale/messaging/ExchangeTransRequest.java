package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransRequest extends BaseRequest {
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Long customerId;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Double totalAmount;
    private List<@Valid ExchangeTransDetailRequest> lstExchangeDetail;
}
