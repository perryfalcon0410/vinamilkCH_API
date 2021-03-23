package vn.viettel.core.db.entity.promotionEntity;

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
    private int saleQty;
    @Column(name ="SALE_UOM")
    private String saleUom;
    @Column(name ="SALE_AMT")
    private int saleAmt;
    @Column(name ="DISC_AMT")
    private int discAmt;
    @Column(name ="DISC_PER")
    private float disPer;
    @Column(name ="FREE_PRODUCT_ID")
    private Long freeProductId;
    @Column(name ="FREE_QTY")
    private int freeQty;
    @Column(name ="FREE_UOM")
    private int freeUom;
    @Column(name ="REQUIRED")
    private int required;
    @Column(name ="SALE_PER")
    private float salePer;
    @Column(name ="ORDER_NUMBER")
    private int orderNumber;
}
