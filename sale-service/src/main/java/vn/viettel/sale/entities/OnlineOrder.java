package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ONLINE_ORDER")
public class OnlineOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "CREATE_DATE") // sale order create date
    private Date createDate;
    @Column(name = "SYN_STATUS")
    private Integer synStatus;
    @Column(name = "VNM_SYN_STATUS")
    private Integer vnmSynStatus;
    @Column(name = "VNM_SYN_TIME")
    private Date vnmSynTime;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "ORDER_ID")
    private Long orderId;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "TOTAL_LINE_VALUE")
    private Float totalLineValue;
    @Column(name = "DISCOUNT_CODE")
    private String discountCode;
    @Column(name = "DISCOUNT_VALUE")
    private Float discountValue;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "CUSTOMER_ADDRESS")
    private String customerAddress;
    @Column(name = "CUSTOMER_PHONE")
    private String customerPhone;
    @Column(name = "CUSTOMER_BIRTHDAY")
    private Date customerDOB;
    @Column(name = "SHIPPING_ADDRESS")
    private String shippingAddress;
    @Column(name = "VAT_INVOICE")
    private String vatInvoice;
    @Column(name = "ORDER_STATUS")
    private String orderStatus;
    @Column(name = "SOURCE_NAME")
    private String sourceName;
    @Column(name = "NOTE")
    private String note;
}
