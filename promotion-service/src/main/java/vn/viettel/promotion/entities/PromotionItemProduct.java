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
@Table(name = "PROMOTION_ITEM_PRODUCT")
public class PromotionItemProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_ITEM_GROUP_ID")
    private Long promotionItemGroupId;
    @Column(name ="PRODUCT_ID")
    private Long productId;
    @Column(name ="STATUS")
    private Integer status;
}
