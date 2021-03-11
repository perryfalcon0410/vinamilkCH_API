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
@Table(name = "SALE_ORDERS")
public class SaleOrder extends BaseEntity{
    @Column(name = "CUS_ID")
    private long cusId;
    @Column(name = "CODE")
    private long code;
    @Column(name = "RECEIPT_TYPE_ID")
    private long receiptTypeId;
    @Column(name = "RECEIPT_ONLINE_ID")
    private long receiptOnlineId;
    @Column(name = "DELIVERY_TYPE")
    private boolean deliveryType;
    @Column(name = "TOTAL_PAYMENT")
    private int totalPayment;
    @Column(name = "PAYMENT_METHOD")
    private int paymentMethod;
    @Column(name = "RED_RECEIPT_EXPORT")
    private boolean redReceiptExport;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DELETED_BY")
    private Long deletedBy;
}

