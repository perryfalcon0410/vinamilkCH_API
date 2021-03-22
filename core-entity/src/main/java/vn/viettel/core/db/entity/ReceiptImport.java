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
@Table(name = "RECEIPT_IMPORTS")
public class ReceiptImport extends BaseEntity {


    @Column(name = "WARE_HOUSE_ID")
    private Long wareHouseId;

    @Column(name = "PO_NUMBER")
    private String poNumber;

    @Column(name = "RECEIPT_IMPORT_CODE")
    private String receiptImportCode;

    @Column(name = "RECEIPT_IMPORT_QUANTITY")
    private Integer receiptImportQuantity;

    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;

    @Column(name = "RECEIPT_IMPORT_DATE")
    private Timestamp receiptImportDate;

    @Column(name = "INVOICE_DATE")
    private Timestamp invoiceDate;

    @Column(name = "RECEIPT_IMPORT_TOTAL")
    private Float receiptImportTotal;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "RECEIPT_IMPORT_TYPE")
    private Integer receiptImportType;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;


}
