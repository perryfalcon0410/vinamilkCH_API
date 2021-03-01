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
@Table(name = "customer_report")
public class CustomerReport extends BaseEntity{
    @Column(name = "cus_id")
    private long cusId;
    @Column(name = "monthlu_purchase")
    private int monthlyPurchase;
    @Column(name = "total_purchase")
    private int totalPurchase;
    @Column(name = "pro_id_1")
    private long proId1;
    @Column(name = "pro_id_2")
    private long proId2;
    @Column(name = "pro_id_3")
    private long proId3;
    @Column(name = "pro_id_4")
    private long proId4;
    @Column(name = "pro_id_5")
    private long proId5;
    @Column(name = "last_trade")
    private LocalDateTime lastTrade;
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;
}
