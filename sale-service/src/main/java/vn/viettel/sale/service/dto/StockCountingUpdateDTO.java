package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockCountingUpdateDTO {
    private Long productId;
    private Integer packageQuantity;
    private Integer unitQuantity;
    private Integer convfact;
}
