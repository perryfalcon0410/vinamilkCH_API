package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.Constants;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransRequest extends BaseRequest {
    @ApiModelProperty(notes = "Số giao dịch")
    private String transCode;
    @ApiModelProperty(notes = "Ngày giao dịch")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Id lý do")
    private Long reasonId;
    @ApiModelProperty(notes = "Lý do")
    private String reason;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Tổng tiền")
    private Double totalAmount;
    @ApiModelProperty(notes = "Request đổi trả hàng")
    private List<@Valid ExchangeTransDetailRequest> lstExchangeDetail;
}
