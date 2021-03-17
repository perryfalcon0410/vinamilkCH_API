package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDERS")
public class SaleOrder extends BaseEntity{
    @Column(name = "CUS_ID")
    private Long cusId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "RECEIPT_TYPE_ID")
    private Long receiptTypeId;
    @Column(name = "SALE_ORDER_DETAIL_ID")
    private Long saleOrderDetailId;
    @Column(name = "SALE_ORDER_TYPE_ID")
    private Long saleOrderTypeId;
    @Column(name = "RECEIPT_ONLINE_ID")
    private Long receiptOnlineId;
    @Column(name = "RED_INVOICE_ID")
    private Long redInvoiceId;
    @Column(name = "DELIVERY_TYPE")
    private boolean deliveryType;
    @Column(name = "TOTAL_PAYMENT")
    private int totalPayment;
    @Column(name = "PAYMENT_METHOD")
    private int paymentMethod;
    @Column(name = "PAYMENT_ID")
    private Long paymentId;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "RED_RECEIPT_NOTE")
    private String redReceiptNote;
    @Column(name = "RED_RECEIPT_EXPORT")
    private boolean redReceiptExport;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DELETED_BY")
    private Long deletedBy;
}

