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
@Table(name = "PROMOTION_CUST_ATTR_DETAIL")
public class PromotionCustATTRDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_CUST_ATTR_ID")
    private Long promotionCustAttrId;
    @Column(name ="OBJECT_TYPE")
    private Integer objectType;
    @Column(name ="OBJECT_ID")
    private Long objectId;
    @Column(name ="STATUS")
    private Integer status;
}
