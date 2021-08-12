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
@Table(name = "PROMOTION_CUST_ATTR")
public class PromotionCustATTR extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
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
