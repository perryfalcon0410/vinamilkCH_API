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
@Table(name = "PROMOTION_PRODUCT_OPEN")
public class PromotionProductOpen extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="PRODUCT_ID")
    private Long productId;
    @Column(name ="QUANTITY")
    private Float quantity;
    @Column(name ="AMOUNT")
    private Float amount;
    @Column(name ="STATUS")
    private Integer status;
}
