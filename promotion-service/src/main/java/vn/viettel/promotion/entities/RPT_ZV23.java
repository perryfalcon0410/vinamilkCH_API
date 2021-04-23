package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RPT_Z23")
public class RPT_ZV23 extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name ="PROMOTION_PROGRAM_ID")
    private Long promotionProgramId;
    @Column(name ="PROMOTION_PROGRAM_CODE")
    private String promotionProgramCode;
    @Column(name ="FROM_DATE")
    private Timestamp fromDate;
    @Column(name ="TO_DATE")
    private Timestamp toDate;
    @Column(name ="SHOP_ID")
    private Long shopId;
    @Column(name ="CUSTOMER_ID")
    private Long customerId;
    @Column(name ="TOTAL_QUANTITY")
    private Long totalQuantity;
    @Column(name ="TOTAL_AMOUNT")
    private Long totalAmount;
}
