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
@Table(name = "SALE_ORDER_DISCOUNT")
public class SaleOrderDiscount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    @Column(name = "PROMOTION_NAME")
    private String promotionName;
    @Column(name = "PROMOTION_TYPE")
    private String promotionType;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "IS_AUTO_PROMOTION")
    private Boolean isAutoPromotion;
    @Column(name = "DISCOUNT_AMOUNT")
    private Double discountAmount;
    @Column(name = "DISCOUNT_AMOUNT_NOT_VAT")
    private Double discountAmountNotVat;
    @Column(name = "DISCOUNT_AMOUNT_VAT")
    private Double discountAmountVat;
    @Column(name = "MAX_DISCOUNT_AMOUNT")
    private Double maxDiscountAmount;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;
}
