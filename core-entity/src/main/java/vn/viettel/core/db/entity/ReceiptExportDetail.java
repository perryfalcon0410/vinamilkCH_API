package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RECEIPT_EXPORT_DETAILS")
public class ReceiptExportDetail extends BaseEntity{
    @Column(name = "RECEIPT_EXPORT_ID")
    private Long receiptExportId;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_PRICE")
    private Float productPrice;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE_TOTAL")
    private Float priceTotal;


    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;
}
