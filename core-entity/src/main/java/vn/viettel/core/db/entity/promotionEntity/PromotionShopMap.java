package vn.viettel.core.db.entity.promotionEntity;

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
@Table(name = "PROMOTION_SHOP_MAP")
public class PromotionShopMap extends BaseEntity {
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="SHOP_ID")
    private Long shopId;
    @Column(name ="QUANTITY_MAX")
    private Long quantityMax;
    @Column(name ="QUANTITY_RECEIVED")
    private Long quantityReceived;
    @Column(name ="QUANTITY_CUSTOMER")
    private int quantityCustomer;
    @Column(name ="AMOUNT_MAX")
    private float amountMax;
    @Column(name ="AMOUNT_RECEIVED")
    private float amountReceived;
    @Column(name ="FROM_DATE")
    private Timestamp fromDate;
    @Column(name ="TO_DATE")
    private Timestamp toDate;
    @Column(name ="STATUS")
    private int status;
    @Column(name ="IS_QUANTITY_MAX_EDIT")
    private int isQuantityMaxEdit;
}
