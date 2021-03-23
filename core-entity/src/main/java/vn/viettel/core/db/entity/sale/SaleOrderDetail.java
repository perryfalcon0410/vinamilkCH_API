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
@Table(name = "SALE_ORDER_DETAIL")
public class SaleOrderDetail extends BaseEntity {
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "IS_FREE_ITEM")
    private Boolean isFreeItem;
    @Column(name = "AUTO_PROMOTION")
    private Float autoPromotion;
    @Column(name = "ZM_PROMOTION")
    private Float zmPromotion;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "AUTO_PROMOTION_NOT_VAT")
    private Float autoPromotionNotVat;
    @Column(name = "AUTO_PROMOTION_VAT")
    private Float autoPromotionVat;
    @Column(name = "ZM_PROMOTION_NOT_VAT")
    private Float zmPromotionNotVat;
    @Column(name = "ZM_PROMOTION_VAT")
    private Float zmPromotionVat;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    @Column(name = "PROMOTION_NAME")
    private String promotionName;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
