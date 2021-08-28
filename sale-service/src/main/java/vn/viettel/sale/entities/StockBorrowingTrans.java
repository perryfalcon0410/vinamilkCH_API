package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_BORROWING_TRANS")
public class StockBorrowingTrans extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private LocalDateTime transDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "STOCK_BORROWING_ID")
    private Long stockBorrowingId;
    @Column(name = "FROM_SHOP_ID")
    private Long fromShopId;
    @Column(name = "TO_SHOP_ID")
    private Long toShopId;

    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "BORROW_DATE")
    private LocalDateTime borrowDate;
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @Column(name = "STATUS")
    private Integer status;
}
