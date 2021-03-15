package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RECEIPT_EXPORTS")
public class ReceiptExport extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "WARE_HOUSE_ID")
    private WareHouse wareHouse;

    @Column(name = "PO_NUMBER")
    private String poNumber;

    @Column(name = "RECEIPT_EXPORT_CODE")
    private String receiptExportCode;

    @Column(name = "RECEIPT_EXPORT_QUANTITY")
    private Integer receiptExportQuantity;

    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;

    @Column(name = "RECEIPT_EXPORT_DATE")
    private Timestamp receiptExportDate;

    @Column(name = "INVOICE_DATE")
    private Timestamp invoiceDate;

    @Column(name = "RECEIPT_EXPORT_TOTAL")
    private Float receiptExportTotal;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "RECEIPT_EXPORT_TYPE")
    private Integer receiptExportType;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;


}
