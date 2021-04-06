package vn.viettel.core.db.entity.stock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_BORROWING")
public class StockBorrowing extends BaseEntity {
    @Column(name = "PO_BORROW_CODE")
    private String poBorrowCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TO_SHOP_ID")
    private Long toShopId;
    @Column(name = "BORROW_DATE")
    private Date borrowDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;

}
