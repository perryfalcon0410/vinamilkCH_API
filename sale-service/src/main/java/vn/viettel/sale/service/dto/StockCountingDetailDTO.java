package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDetailDTO extends BaseDTO {
    private Long stockCountingId;
    private Long shopId;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Integer stockQuantity;
}
