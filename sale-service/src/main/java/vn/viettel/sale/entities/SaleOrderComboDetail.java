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
@Table(name = "SALE_ORDER_COMBO_DETAIL")
public class SaleOrderComboDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "COMBO_QUANTITY")
    private Integer comboQuantity;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "AMOUNT")
    private Double amount;
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "AUTO_PROMOTION")
    private Double autoPromotion;
    @Column(name = "AUTO_PROMOTION_NOT_VAT")
    private Double autoPromotionNotVat;
    @Column(name = "AUTO_PROMOTION_VAT")
    private Double autoPromotionVat;
    @Column(name = "ZM_PROMOTION")
    private Double zmPromotion;
    @Column(name = "ZM_PROMOTION_NOT_VAT")
    private Double zmPromotionNotVat;
    @Column(name = "ZM_PROMOTION_VAT")
    private Double zmPromotionVat;
    @Column(name = "PRICE_NOT_VAT")
    private Double priceNotVat;
    @Column(name = "IS_FREE_ITEM")
    private Boolean isFreeItem;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    @Column(name = "PROMOTION_NAME")
    private String promotionName;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;

}
