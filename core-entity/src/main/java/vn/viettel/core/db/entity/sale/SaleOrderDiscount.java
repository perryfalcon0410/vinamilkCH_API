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
@Table(name = "SALE_ORDER_DISCOUNT")
public class SaleOrderDiscount extends BaseEntity {
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
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
    @Column(name = "MAX_DISCOUNT_AMOUNT")
    private Float maxDiscountAmount;
    @Column(name = "LEVEL_NUMBER")
    private Integer levelNumber;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
