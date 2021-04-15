package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalStockCounting {
    private Integer inventoryTotal;
    private Integer stockTotal;
    private Float totalAmount;
    private Integer totalPacket;
    private Integer totalUnit;
    private Integer changeQuantity;
}
