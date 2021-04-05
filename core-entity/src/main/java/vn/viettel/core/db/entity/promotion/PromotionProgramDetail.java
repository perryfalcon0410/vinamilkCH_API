package vn.viettel.core.db.entity.promotion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PROMOTION_PROGRAM_DETAIL")
public class PromotionProgramDetail extends BaseEntity {
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="PRODUCT_ID")
    private Long productId;
    @Column(name ="SALE_QTY")
    private Integer saleQty;
    @Column(name ="SALE_UOM")
    private String saleUom;
    @Column(name ="SALE_AMT")
    private Integer saleAmt;
    @Column(name ="DISC_AMT")
    private Integer discAmt;
    @Column(name ="DISC_PER")
    private Float disPer;
    @Column(name ="FREE_PRODUCT_ID")
    private Long freeProductId;
    @Column(name ="FREE_QTY")
    private Integer freeQty;
    @Column(name ="FREE_UOM")
    private String freeUom;
    @Column(name ="REQUIRED")
    private Integer required;
    @Column(name ="SALE_PER")
    private Float salePer;
    @Column(name ="ORDER_NUMBER")
    private Integer orderNumber;
}
