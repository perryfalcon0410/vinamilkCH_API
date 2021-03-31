package vn.viettel.core.db.entity.promotion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PROMOTION_PROGRAM")
public class PromotionProgram extends BaseEntity {
    @Column(name = "PROMOTION_PROGRAM_CODE")
    private String promotionProgramCode;
    @Column(name = "PROMOTION_PROGRAM_NAME")
    private String promotionProgramName;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "PRO_FORMAT")
    private String proFormat;
    @Column(name = "FROM_DATE")
    private Timestamp fromDate;
    @Column(name = "TO_DATE")
    private Timestamp toDate;
    @Column(name = "RELATION")
    private Integer relation;
    @Column(name = "MULTIPLE")
    private Integer multiple;
    @Column(name = "RECURSIVE")
    private Integer recursive;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "GROUP_ID")
    private Long groupId;
    @Column(name = "IS_RETURN")
    private Integer isReturn;
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;
    @Column(name = "GIVEN_TYPE")
    private Integer givenType;
    @Column(name = "DISCOUNT_TYPE")
    private Integer discountType;
    @Column(name = "DISCOUNT_PRICE_TYPE")
    private Integer discountPriceType;
    @Column(name = "CHECK_MAIN_ITEM")
    private Integer checkMainItem;
    @Column(name = "PROMOTION_DAY_TIME")
    private Integer promotionDateTime;
    @Column(name = "PROG_TYPE_ID")
    private Long progTypeId;
    @Column(name = "AMOUNT_ORDER_TYPE")
    private Integer amountOrderType;
    @Column(name = "IS_EDITED")
    private Integer isEdited;
    @Column(name = "PROMOTION_GROUP_ID")
    private Long promotionGroupId;
}
