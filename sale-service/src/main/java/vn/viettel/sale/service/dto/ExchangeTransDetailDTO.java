package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDetailDTO extends BaseDTO {
    private Long transId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    private Long shopId;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
}
