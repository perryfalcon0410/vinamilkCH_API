package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PROMOTION_SHOP_MAP")
public class PromotionShopMap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="SHOP_ID")
    private Long shopId;
    @Column(name ="QUANTITY_MAX")
    private Long quantityMax;
    @Column(name ="QUANTITY_RECEIVED")
    private Long quantityReceived;
    @Column(name ="QUANTITY_CUSTOMER")
    private Integer quantityCustomer;
    @Column(name ="AMOUNT_MAX")
    private Float amountMax;
    @Column(name ="AMOUNT_RECEIVED")
    private Float amountReceived;
    @Column(name ="FROM_DATE")
    private LocalDateTime fromDate;
    @Column(name ="TO_DATE")
    private LocalDateTime toDate;
    @Column(name ="STATUS")
    private Integer status;
    @Column(name ="IS_QUANTITY_MAX_EDIT")
    private Integer isQuantityMaxEdit;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @LastModifiedDate
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
