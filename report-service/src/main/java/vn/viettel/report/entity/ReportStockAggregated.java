package vn.viettel.report.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RPT_STOCK_AGGREGATED")
public class ReportStockAggregated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name = "RPT_DATE")
    private Date rptDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long warehouseTypeId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "BEGINNING_QTY")
    private Long beginningQuantity;
    @Column(name = "IMP_QTY")
    private Long impQuantity;
    @Column(name = "IMP_ADJUSTMENT_QTY")
    private Long impAdjustmentQuantity;
    @Column(name = "IMP_BORROWING_QTY")
    private Long impBorrowingQuantity;
    @Column(name = "IMP_RETURN_QTY")
    private Long impReturnQuantity;
    @Column(name = "IMP_COMBO_QTY")
    private Long impComboQuantity;
    @Column(name = "EXP_SALES_QTY")
    private Long expSalesQuantity;
    @Column(name = "EXP_PROMOTION_QTY")
    private Long expPromotionQuantity;
    @Column(name = "EXP_ADJUSTMENT_QTY")
    private Long expAdjustmentQuantity;
    @Column(name = "EXP_BORROWING_QTY")
    private Long expBorrowingQuantity;
    @Column(name = "EXP_EXCHANGE_QTY")
    private Long expExchangeQuantity;
    @Column(name = "EXP_COMBO_QTY")
    private Long expComboQuantity;
    @Column(name = "EXP_PORETURN_QTY")
    private Long expPoreturnQuantity;
    @Column(name = "ENDING_QTY")
    private Long endingQuantity;
    @Column(name = "CREATED_AT")
    private Date createAt;
    @Column(name = "IMP_RETURN_SALE_QTY")
    private Long impReturnSaleQuantity;
    @Column(name = "IMP_RETURN_PRO_QTY")
    private Long impReturnProQuantity;
    
}
