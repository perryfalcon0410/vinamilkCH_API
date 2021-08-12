package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_COUNTING")
public class StockCounting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "STOCK_COUNTING_CODE")
    private String stockCountingCode;
    @Column(name = "COUNTING_DATE")
    private LocalDateTime countingDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;

}
