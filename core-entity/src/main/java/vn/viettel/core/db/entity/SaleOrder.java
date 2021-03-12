package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDERS")
public class SaleOrder extends BaseEntity{
    @Column(name = "CUS_ID")
    private long cusId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "SALE_ORDER_DETAIL_ID")
    private long saleOrderDetailId;
    @Column(name = "RECEIPT_TYPE_ID")
    private long receiptTypeId;
    @Column(name = "RED_RECEIPT_EXPORT")
    private boolean redReceiptExport;
    @Column(name = "RECEIPT_ONLINE_ID")
    private long receiptOnlineId;
    @Column(name = "DELIVERY_TYPE")
    private boolean deliveryType;
    @Column(name = "TOTAL_PAYMENT")
    private int totalPayment;
    @Column(name = "PAYMENT_METHOD")
    private int paymentMethod;
    @Column(name = "RED_RECEIPT_NOTE")
    private String redReceiptNote;
    @Column(name = "CREATED_BY")
    private long created_by;
    @Column(name = "UPDATED_BY")
    private long updated_by;
    @Column(name = "DELETED_BY")
    private long deleted_by;
}
