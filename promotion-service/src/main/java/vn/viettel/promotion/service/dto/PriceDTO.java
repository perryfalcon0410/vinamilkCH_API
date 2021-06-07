package vn.viettel.promotion.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PriceDTO extends BaseDTO {
    private Long productId;
    private Integer priceType;
    private Long customerTypeId;
    private Double price;
    private Double priceNotVat;
    private LocalDateTime fromDate;
    private Double vat;
    private LocalDateTime toDate;
    private Integer status;
}
