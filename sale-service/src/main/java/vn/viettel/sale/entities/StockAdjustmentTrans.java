package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_ADJUSTMENT_TRANS")
public class StockAdjustmentTrans extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "ADJUSTMENT_ID")
    private Long adjustmentId;
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "ADJUSTMENT_DATE")
    private Date adjustmentDate;
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "ORDER_DATE ")
    private Date orderDate;

    @Column(name = "REASON_ID")
    private Long reasonId;
}
