package vn.viettel.core.db.entity.sale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDERS")
public class SaleOrder extends BaseEntity {
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "SALESMAN_ID")
    private Long salemanId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "TOTAL_PROMOTION")
    private Float totalPromotion;
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "TOTAL_PAID")
    private Float totalPaid;
    @Column(name = "BALANCE")
    private Float balance;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "FROM_SALE_ORDER_ID")
    private Long fromSaleOrderId;
    @Column(name = "MEMBERCARD_AMOUNT")
    private Float memberCardAmount;
    @Column(name = "TOTAL_VOUCHER")
    private Float totalVoucher;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "DELIVERY_TYPE")
    private Integer deliveryType;
    @Column(name = "TOTAL_CUS_PURCHASE")
    private Float totalCustomerPurchase;
    @Column(name = "ORDER_TYPE")
    private Integer orderType;
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @Column(name = "AUTO_PROMOTION_NOT_VAT")
    private Float autoPromotionNotVat;
    @Column(name = "AUTO_PROMOTION_VAT")
    private Float autoPromotionVat;
    @Column(name = "AUTO_PROMOTION")
    private Float autoPromotion;
    @Column(name = "ZM_PROMOTION")
    private Float zmPromotion;
    @Column(name = "TOTAL_PROMOTION_NOT_VAT")
    private Float totalPromotionNotVat;
    @Column(name = "CUS_PURCHASE")
    private Float customerPurchase;
    @Column(name = "F1_NUMBER")
    private String f1Number;
    @Column(name = "DISCOUNT_CODE_AMOUNT")
    private Float discountCodeAmount;
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
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "REASON_ID")
    private Long reasonId;
    @Column(name = "REASON_DESC")
    private String reasonDesc;
}
