package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "receiptimport")
public class ReceiptImport extends BaseEntity {
    @ManyToOne
    @Column(name = "warehouse_id")
    private WareHouse wareHouse;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "receipt_code")
    private String receiptCode;

    @Column(name = "receipt_quantity")
    private Integer receiptQuantity;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "internal_number")
    private String internalNumber;

    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "receipt_total")
    private Float receiptTotal;

    @Column(name = "note")
    private String note;

    @Column(name = "receipt_type")
    private Integer receiptType;

    @Column(name = "status")
    private Integer status;


}
