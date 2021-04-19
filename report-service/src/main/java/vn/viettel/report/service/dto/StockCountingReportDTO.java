package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockCountingReportDTO {
    private String productCategory;
    private String productCode;
    private String productName;
    private Integer stockQuantity;
    private Integer packetQuantity;
    private Integer unitQuantity;
    private Float price;
    private Float totalAmount;
    private Integer convfact;
    private String packetUnit;
    private String unit;
    private String shop;
    private String shopType;
    private String productGroup;
    private Integer minInventory;
    private Integer maxInventory;
    private String warning;
}
