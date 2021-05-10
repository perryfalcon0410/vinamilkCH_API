package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ImportExportInventoryDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "CAT_NAME")
    private String catName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UOM")
    private String uom;

    @Column(name = "BEGINNING_QUANTITY")
    private Integer beginningQuantity;
    @Column(name = "BEGINNING_PRICE")
    private Float beginningPrice;
    @Column(name = "BEGINNING_AMOUNT")
    private Float beginningAmount;

    @Column(name = "IMPORT_TOTAL_QUANTITY")
    private Integer importTitalQuantity;
    @Column(name = "IMPORT_QUANTITY")
    private Integer importQuantity;
    @Column(name = "IMPORT_AMOUNT")
    private Float importAmount;
    @Column(name = "IMPORT_ADJUSTMENT_QUANTITY")
    private Integer importAdjustmentQuantity;
    @Column(name = "IMPORT_ADJUSTMENT_AMOUNT")
    private Float importAdjustmentAmount;
    @Column(name = "IMPORT_BORROWING_QUANTITY")
    private Integer importBorrowingQuantity;
    @Column(name = "IMPORT_BORROWING_AMOUNT")
    private Float importBorrowingAmount;
    @Column(name = "IMPORT_SALES_RETURN_QUANTITY")
    private Integer importSalesReturnQuantity;
    @Column(name = "IMPORT_SALES_RETURN_AMOUNT")
    private Float importSalesReturnAmount;
    @Column(name = "IMPORT_PROMOTION_RETURN_QUANTITY")
    private Integer importPromotionReturnQuantity;
    @Column(name = "IMPORT_PROMOTION_RETURN_AMOUNT")
    private Float importPromotionReturnAmount;

    @Column(name = "EXPORT_TOTAL_QUANTITY")
    private Integer exportTotalQuantity;
    @Column(name = "EXPORT_SALES_QUANTITY")
    private Integer exportSalesQuantity;
    @Column(name = "EXPORT_SALES_AMOUNT")
    private Float exportSalesAmount;
    @Column(name = "EXPORT_PROMOTION_QUANTITY")
    private Integer exportPromotionQuantity;
    @Column(name = "EXPORT_PROMOTION_AMOUNT")
    private Float exportPromotionAmount;
    @Column(name = "EXPORT_ADJUSTMENT_QUANTITY")
    private Integer exportAdjustmentQuantity;
    @Column(name = "EXPORT_ADJUSTMENT_AMOUNT")
    private Float exportAdjustmentAmount;
    @Column(name = "EXPORT_BORROWING_QUANTITY")
    private Integer exportBorrowingQuantity;
    @Column(name = "EXPORT_BORROWING_AMOUNT")
    private Float exportBorrowingAmount;
    @Column(name = "EXPORT_EXCHANGE_QUANTITY")
    private Integer exportExchangeQuantity;
    @Column(name = "EXPORT_EXCHANGE_AMOUNT")
    private Float exportExchangeAmount;

    @Column(name = "ENDING_QUANTITY")
    private Integer endingQuantity;
    @Column(name = "ENDING_PRICE")
    private Float endingPrice;
    @Column(name = "ENDING_AMOUNT")
    private Float endingAmount;
}
