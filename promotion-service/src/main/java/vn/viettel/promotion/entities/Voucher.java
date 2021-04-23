package vn.viettel.promotion.entities;

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
@Table(name = "VOUCHERS")
public class Voucher extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "VOUCHER_CODE")
    private String voucherCode;
    @Column(name = "VOUCHER_NAME")
    private String voucherName;
    @Column(name = "VOUCHER_NAME_TEXT")
    private String voucherNameText;
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "SERIAL")
    private String serial;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "CUSTOMER_TYPE_CODE")
    private String customerTypeCode;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "CHANGE_DATE")
    private Date changeDate;
    @Column(name = "CHANGE_USER")
    private String changeUser;
    @Column(name = "ACTIVATED")
    private Boolean activated;
    @Column(name = "ACTIVATED_DATE")
    private Date activatedDate;
    @Column(name = "ACTIVATED_USER")
    private String activatedUser;
    @Column(name = "IS_USED")
    private Boolean isUsed;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "PRICE_USED")
    private Float priceUsed;
    @Column(name = "ORDER_CUSTOMER_CODE")
    private String orderCustomerCode;
    @Column(name = "ORDER_AMOUNT")
    private Float orderAmount;
    @Column(name = "ORDER_SHOP_CODE")
    private String orderShopCode;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "PAYMENT_STATUS")
    private Integer paymentStatus;
    @Column(name = "UPDATE_USER")
    private String updateUser;

}



