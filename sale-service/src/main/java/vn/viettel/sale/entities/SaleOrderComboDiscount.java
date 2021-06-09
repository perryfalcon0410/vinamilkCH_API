package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDER_COMBO_DISCOUNT")
public class SaleOrderComboDiscount extends BaseEntity {
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
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "IS_AUTO_PROMOTION")
    private Boolean isAutoPromotion;
    @Column(name = "DISCOUNT_AMOUNT")
    private Float MAX_DISCOUNT_AMOUNT;
    @Column(name = "DISCOUNT_AMOUNT_NOT_VAT")
    private Float discountAmountNotVat;
    @Column(name = "DISCOUNT_AMOUNT_VAT")
    private Float discountAmountVat;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;

}
