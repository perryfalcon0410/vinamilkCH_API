package vn.viettel.sale.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RPT_STOCK_AGGREGATED")
public class RptStockAggregated extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "RPT_DATE")
    private LocalDateTime rptDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long warehouseTypeId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "BEGINNING_QUANTITY")
    private Long beginningQuantity;

    @Column(name = "IMPORT_QUANTITY")
    private Long importQuantity;
    @Column(name = "IMPORT_ADJUSTMENT_QUANTITY")
    private Float importAdjustmentQuantity;
    @Column(name = "IMPORT_BORROWING_QUANTITY")
    private Long importBorrowingQuantity;
    @Column(name = "IMPORT_SALES_RETURN_QUANTITY")
    private Float importSalesReturnQuantity;
    @Column(name = "IMPORT_PROMOTION_RETURN_QUANTITY")
    private Long importPromotionReturnQuantity;

    @Column(name = "EXPORT_SALES_QUANTITY")
    private Long exportSalesQuantity;;
    @Column(name = "EXPORT_PROMOTION_QUANTITY")
    private Float exportPromotionQuantity;
    @Column(name = "EXPORT_ADJUSTMENT_QUANTITY")
    private Long exportAdjustmentQuantity;
    @Column(name = "EXPORT_BORROWING_QUANTITY")
    private Long exportBorrowingQuantity;;
    @Column(name = "EXPORT_EXCHANGE_QUANTITY")
    private Float exportExchangeQuantity;

    @Column(name = "ENDING_QUANTITY")
    private Long endingQuantity;
}
