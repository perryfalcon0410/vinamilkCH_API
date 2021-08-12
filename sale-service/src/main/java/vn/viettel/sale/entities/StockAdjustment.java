package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_ADJUSTMENT")
public class StockAdjustment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "ADJUSTMENT_CODE")
    private String adjustmentCode;
    @Column(name = "ADJUSTMENT_DATE")
    private LocalDateTime adjustmentDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "REASON_ID")
    private Long reasonId;
    @Column(name = "DESCRIPTION")
    private String description;
    @Formula("(SELECT w.WAREHOUSE_TYPE_NAME FROM WAREHOUSE_TYPE w WHERE w.id = WAREHOUSE_TYPE_ID )")
    private String wareHouseTypeName;

}
