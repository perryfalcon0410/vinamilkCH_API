package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDetailDTO extends BaseDTO {
    private Long stockCountingId;
    private String productCategory;
    private Long warehouseTypeId;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Float price;
    private Integer stockQuantity;
    private Integer inventoryQuantity;
    private Integer changeQuantity;
    private String totalAmount;
    private Integer convfact;
    private String packetUnit;
    private String unit;
    private Integer packetQuantity;
    private Integer unitQuantity;
}
