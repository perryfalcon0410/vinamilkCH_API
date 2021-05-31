package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

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
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer quantity;
    @MaxTextLength(length = 12, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Float totalAmount;
    private List<@Valid ExchangeTransDetailRequest> lstExchangeDetail;
}
