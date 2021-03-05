package vn.viettel.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "receiptimport")
public class ReceiptImport extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
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

    @ApiModelProperty(notes = "0.do not delete promotional products" +
            "1.delete promotional products")
    @Column(name = "object_type")
    private Integer objectType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;


}
