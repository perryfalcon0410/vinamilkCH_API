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




}
