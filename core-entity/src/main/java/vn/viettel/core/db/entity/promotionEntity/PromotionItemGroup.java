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
@Table(name = "PROMOTION_ITEM_GROUP")
public class PromotionItemGroup extends BaseEntity {
    @Column(name ="ITEM_GROUP_CODE")
    private String itemGroupCode;
    @Column(name ="ITEM_GROUP_NAME")
    private String itemGroupName;
    @Column(name ="DESCRIPTION")
    private String description;
    @Column(name ="NOT_ACCUMULATED")
    private int notAccumulated;
    @Column(name ="STATUS")
    private int status;
}
