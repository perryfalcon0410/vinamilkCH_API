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
@Table(name = "STOCK_BORROWING")
public class StockBorrowing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PO_BORROW_CODE")
    private String poBorrowCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TO_SHOP_ID")
    private Long toShopId;
    @Column(name = "BORROW_DATE")
    private LocalDateTime borrowDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "STATUS_IMPORT")
    private Integer statusImport;
    @Column(name = "STATUS_EXPORT")
    private Integer statusExport;
}
