package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDERS")
public class SaleOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "SALESMAN_ID")
    private Long salemanId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "AMOUNT")
    private Double amount;
    @Column(name = "TOTAL_PROMOTION")
    private Double totalPromotion;
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "TOTAL_PAID")
    private Double totalPaid;
    @Column(name = "BALANCE")
    private Double balance;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "FROM_SALE_ORDER_ID")
    private Long fromSaleOrderId;
    @Column(name = "MEMBERCARD_AMOUNT")
    private Double memberCardAmount;
    @Column(name = "TOTAL_VOUCHER")
    private Double totalVoucher;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "DELIVERY_TYPE")
    private Integer deliveryType;
    @Column(name = "TOTAL_CUS_PURCHASE")
    private Double totalCustomerPurchase;
    @Column(name = "ORDER_TYPE")
    private Integer orderType;
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @Column(name = "AUTO_PROMOTION_NOT_VAT")
    private Double autoPromotionNotVat;
    @Column(name = "AUTO_PROMOTION_VAT")
    private Double autoPromotionVat;
    @Column(name = "AUTO_PROMOTION")
    private Double autoPromotion;
    @Column(name = "ZM_PROMOTION")
    private Double zmPromotion;
    @Column(name = "TOTAL_PROMOTION_NOT_VAT")
    private Double totalPromotionNotVat;
    @Column(name = "CUS_PURCHASE")
    private Double customerPurchase;
    @Column(name = "F1_NUMBER")
    private String f1Number;
    @Column(name = "DISCOUNT_CODE_AMOUNT")
    private Double discountCodeAmount;
    @Column(name = "ONL_SUBTYPE")
    private Integer onlineSubType;
    @Column(name = "USED_RED_INVOICE")
    private Boolean usedRedInvoice;
    @Column(name = "RED_INVOICE_COMPANYNAME")
    private String redInvoiceCompanyName;
    @Column(name = "RED_INVOICE_TAXCODE")
    private String redInvoiceTaxCode;
    @Column(name = "RED_INVOICE_ADDRESS")
    private String radInvoiceAddress;
    @Column(name = "RED_INVOICE_REMARK")
    private String redInvoiceRemark;
    @Column(name = "REASON_ID")
    private String reasonId;
    @Column(name = "REASON_DESC")
    private String reasonDesc;

    public void setAutomatePromotion(Double autoPromotion) {
        if(this.autoPromotion == null)
            this.autoPromotion = 0D;
        this.autoPromotion += autoPromotion;
    }

    public void setOrderAmount(Double amount) {
        if (this.amount == null)
            this.amount = 0D;
        this.amount += amount;
    }
}
