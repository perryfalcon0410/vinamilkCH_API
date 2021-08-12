package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PROMOTION_PROGRAM_DETAIL")
public class PromotionProgramDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="PRODUCT_ID")
    private Long productId;
    @Column(name ="SALE_QTY")
    private Integer saleQty;
    @Column(name ="SALE_UOM")
    private String saleUom;
    @Column(name ="SALE_AMT")
    private Double saleAmt;
    @Column(name ="DISC_AMT")
    private Double discAmt;
    @Column(name ="DISC_PER")
    private Double disPer;
    @Column(name ="FREE_PRODUCT_ID")
    private Long freeProductId;
    @Column(name ="FREE_QTY")
    private Integer freeQty;
    @Column(name ="FREE_UOM")
    private String freeUom;
    @Column(name ="REQUIRED")
    private Integer required;
    @Column(name ="SALE_PER")
    private Double salePer;
    @Column(name ="ORDER_NUMBER")
    private Integer orderNumber;
}
