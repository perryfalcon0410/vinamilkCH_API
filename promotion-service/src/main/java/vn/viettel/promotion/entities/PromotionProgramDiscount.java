package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PROMOTION_PROGRAM_DISCOUNT")
public class PromotionProgramDiscount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="MIN_SALE_AMOUNT")
    private Double minSaleAmount;
    @Column(name ="MAX_SALE_AMOUNT")
    private Double maxSaleAmount;
    @Column(name ="DISCOUNT_AMOUNT")
    private Double discountAmount;
    @Column(name ="DISCOUNT_PERCENT")
    private Double discountPercent;
    @Column(name ="MAX_DISCOUNT_AMOUNT")
    private Double maxDiscountAmount;
    @Column(name ="STATUS")
    private Integer status;
    @Column(name ="DISCOUNT_CODE")
    private String discountCode;
    @Column(name ="TYPE")
    private Integer type;
    @Column(name ="IS_USED")
    private Integer isUsed;
    @Column(name ="ORDER_DATE")
    private Timestamp orderDate;
    @Column(name ="ORDER_NUMBER")
    private String orderNumber;
    @Column(name ="ORDER_SHOP_CODE")
    private String orderShopCode;
    @Column(name ="ORDER_CUSTOMER_CODE")
    private String orderCustomerCode;
    @Column(name ="ORDER_AMOUNT")
    private Long orderAmount;
    @Column(name ="ACTUAL_DISCOUNT_AMOUNT")
    private Long ActualDiscountAmount;
    @Column(name ="CUSTOMER_CODE")
    private String customerCode;
}
