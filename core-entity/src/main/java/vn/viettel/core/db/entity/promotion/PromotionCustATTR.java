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
@Table(name = "PROMOTION_CUST_ATTR")
public class PromotionCustATTR extends BaseEntity {
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="OBJECT_TYPE")
    private Integer objectType;
    @Column(name ="OBJECT_ID")
    private Long objectId;
    @Column(name ="FROM_VALUE")
    private String fromValue;
    @Column(name ="TO_VALUE")
    private String toValue;
    @Column(name ="STATUS")
    private Integer status;
}
