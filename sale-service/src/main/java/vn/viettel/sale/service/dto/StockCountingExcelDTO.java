package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCountingExcelDTO {

    private Integer index;

    private String productCategory;

    private String productCategoryCode;

    private String productGroup;

    private String productCode;

    private String productName;

    private Integer stockQuantity;

    private Double price;

    private Double totalAmount;

    private Integer packetQuantity;

    private Integer unitQuantity;

    private Integer inventoryQuantity;

    private Integer changeQuantity;

    private String packetUnit;

    private Integer convfact;

    private String unit;

    private String error;

    private Long productId;

    public StockCountingExcelDTO(Long productId,
                                 String productCode, String productName,  String productGroup,
                                 String productCategory, String productCategoryCode,
                                 Double price, Integer stockQuantity,  String unit, String packetUnit,
                                 Integer convfact,
                                 Integer unitQuantity,  Integer packetQuantity) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.productGroup = productGroup;
        this.productCategory = productCategory;
        this.productCategoryCode = productCategoryCode;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.packetUnit = packetUnit;
        this.convfact = convfact;
        this.unitQuantity = unitQuantity;
        this.packetQuantity = packetQuantity;

      /*  this.inventoryQuantity = inventoryQuantity;
        this.changeQuantity = changeQuantity;*/

    }
}
