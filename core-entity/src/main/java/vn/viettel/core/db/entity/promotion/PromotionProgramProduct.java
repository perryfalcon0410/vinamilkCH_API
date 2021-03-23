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
@Table(name = "PROMOTION_PROGRAM_PRODUCT")
public class PromotionProgramProduct extends BaseEntity {
    @Column(name ="ITEM_GROUP_CODE")
    private String itemGroupCode;
    @Column(name ="ITEM_GROUP_NAME")
    private String itemGroupName;
    @Column(name ="DESCRIPTION")
    private String description;
    @Column(name ="NOT_ACCUMULATED")
    private int notAccumlated;
    @Column(name ="STATUS")
    private int status;
}
