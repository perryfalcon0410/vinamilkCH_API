package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDER_DETAIL")
public class SaleOrderDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Double price;

    //tổng tiền trước chiết khấu
    @Column(name = "AMOUNT")
    private Double amount;

    // tổng tiền sau chiết khấu
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "IS_FREE_ITEM")
    private Boolean isFreeItem;
    @Column(name = "AUTO_PROMOTION")
    private Double autoPromotion;
    @Column(name = "ZM_PROMOTION")
    private Double zmPromotion;
    @Column(name = "PRICE_NOT_VAT")
    private Double priceNotVat;
    @Column(name = "AUTO_PROMOTION_NOT_VAT")
    private Double autoPromotionNotVat;
    @Column(name = "AUTO_PROMOTION_VAT")
    private Double autoPromotionVat;
    @Column(name = "ZM_PROMOTION_NOT_VAT")
    private Double zmPromotionNotVat;
    @Column(name = "ZM_PROMOTION_VAT")
    private Double zmPromotionVat;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    //todo
    @Transient
    private String promotionType;
    @Column(name = "PROMOTION_NAME")
    private String promotionName;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;

    @Formula("(SELECT p.PRODUCT_NAME FROM PRODUCTS p WHERE p.id = productId )")
    private String productName;

    @Formula("(SELECT p.PRODUCT_CODE FROM PRODUCTS p WHERE p.id = productId )")
    private String productCode;


}
