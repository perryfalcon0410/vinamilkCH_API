package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "receiptimport")
public class ReceiptImport extends BaseEntity {

    @Column(name = "warehouse_id")
    private Long warehouse_id;

    @Column(name = "receipt_code")
    private String receipt_code;

    @Column(name = "receipt_quantity")
    private Integer receipt_quantity;

    @Column(name = "invoice_number")
    private String invoice_number;

    @Column(name = "internal_number")
    private String internal_number;

    @Column(name = "receipt_date")
    private LocalDateTime receipt_date;

    @Column(name = "invoice_date")
    private LocalDateTime invoice_date;

    @Column(name = "receipt_total")
    private Float receipt_total;

    @Column(name = "note")
    private String note;

    @Column(name = "receipt_type")
    private String receipt_type;

    @Column(name = "status")
    private Integer status;


}
