package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.Constants;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransRequest extends BaseRequest {
    private String transCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private Long shopId;
    private Long customerId;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Double totalAmount;
    private List<@Valid ExchangeTransDetailRequest> lstExchangeDetail;
}
