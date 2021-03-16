package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RECEIPT_EXPORT_BORROW_DETAILS")
public class ReceiptExportBorrowDetail extends BaseEntity{
    @Column(name = "RECEIPT_EXPORT_BORROW_ID")
    private Long    receiptExportBorrowId;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRODUCT_PRICE")
    private Float productPrice;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "PRICE_TOTAL")
    private Float priceTotal;
}
