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
@Table(name = "PROMOTION_CUST_ATTR_DETAIL")
public class PromotionCustATTRDetail extends BaseEntity {
    @Column(name ="PROMOTION_CUST_ATTR_ID")
    private Long promotionCustAttrId;
    @Column(name ="OBJECT_TYPE")
    private int objectType;
    @Column(name ="OBJECT_ID")
    private Long objectId;
    @Column(name ="STATUS")
    private int status;
}
