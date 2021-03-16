package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RED_INVOICES")
public class RedInvoice extends BaseEntity{
    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "OFFICE_WORKING")
    private Long officeWorking;
    @Column(name = "OFFICE_ADDRESS")
    private Long officeAddress;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "PRINT_DATE")
    private Timestamp printDate;
}
