package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StockTotalReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "PRODUCT_CATEGORY")
    private String productCategory;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "QUANTITY")
    private Long stockQuantity;
    @Column(name = "PACKAGE_QUANTITY")
    private Long packetQuantity;
    @Column(name = "UNIT_QUANTITY")
    private Long unitQuantity;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
    @Column(name = "CONVFACT")
    private Integer convfact;
    @Column(name = "PACKAGE_UNIT")
    private String packetUnit;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "SHOP_NAME")
    private String shop;
    @Column(name = "SHOP_TYPE")
    private String shopType;
    @Column(name = "PRODUCT_GROUP")
    private String productGroup;

    @Column(name = "MIN_INVENTORY")
    private Long minInventory;
    @Column(name = "MAX_INVENTORY")
    private Long maxInventory;
    @Column(name = "WARNING")
    private String warning;
    @Column(name = "CAT_ID")
    private Long catId;
}
