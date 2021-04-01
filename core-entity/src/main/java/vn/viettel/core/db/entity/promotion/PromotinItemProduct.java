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
@Table(name = "PROMOTION_ITEM_PRODUCT")
public class PromotinItemProduct extends BaseEntity {
    @Column(name ="PROMOTION_ITEM_GROUP_ID")
    private Long promotionItemGroupId;
    @Column(name ="PRODUCT_ID")
    private Long productId;
    @Column(name ="STATUS")
    private Integer status;
}
