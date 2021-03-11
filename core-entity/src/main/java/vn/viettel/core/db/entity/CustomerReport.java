package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CUSTOMER_REPORTS")
public class CustomerReport extends BaseEntity{
    @Column(name = "CUS_ID")
    private long cusId;
    @Column(name = "MONTHLY_PURCHASE")
    private int monthlyPurchase;
    @Column(name = "TOTAL_PURCHASE")
    private int totalPurchase;
    @Column(name = "PRO_ID_1")
    private long proId1;
    @Column(name = "PRO_ID_2")
    private long proId2;
    @Column(name = "PRO_ID_3")
    private long proId3;
    @Column(name = "PRO_ID_4")
    private long proId4;
    @Column(name = "PRO_ID_5")
    private long proId5;
    @Column(name = "LAST_TRADE")
    private LocalDateTime lastTrade;

}
