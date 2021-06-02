package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeTransDetailDTO extends BaseDTO {
    private Long transId;
    private LocalDateTime transDate;
    private Long shopId;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
}
