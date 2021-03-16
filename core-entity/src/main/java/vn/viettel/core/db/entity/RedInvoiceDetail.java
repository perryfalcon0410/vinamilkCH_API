package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RED_INVOICE_DETAILS")
public class RedInvoiceDetail extends BaseEntity{
    @Column(name = "RED_INVOICE_ID")
    private Long redInvoiceId;
    @Column(name = "SALE_ORDER_DETAIL_ID")
    private Long orderDetailId;
    @Column(name = "NOTE")
    private String note;
}
