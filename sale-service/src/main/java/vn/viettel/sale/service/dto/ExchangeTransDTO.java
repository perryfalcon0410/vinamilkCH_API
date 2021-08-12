package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDTO {
    private Long id;
    private String transCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private Long shopId;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private Long reasonId;
    private String reason;
    private Integer quantity;
    private Double totalAmount;
    private List<ExchangeTransDetailRequest> listProducts;
}
