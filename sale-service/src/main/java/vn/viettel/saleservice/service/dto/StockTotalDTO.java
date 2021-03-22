package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StockTotalDTO extends BaseDTO{

    private Long shopId;
    private Long wareHouseId;
    private Long productId;
    private Integer quantity;
    private Integer availableQuantity;
    private Integer status;
    private String description;
}
