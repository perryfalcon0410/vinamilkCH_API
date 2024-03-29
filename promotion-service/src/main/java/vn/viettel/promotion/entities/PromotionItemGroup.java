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
@Table(name = "PROMOTION_ITEM_GROUP")
public class PromotionItemGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="ITEM_GROUP_CODE")
    private String itemGroupCode;
    @Column(name ="ITEM_GROUP_NAME")
    private String itemGroupName;
    @Column(name ="DESCRIPTION")
    private String description;
    @Column(name ="NOT_ACCUMULATED")
    private Integer notAccumulated;
    @Column(name ="STATUS")
    private Integer status;
}
