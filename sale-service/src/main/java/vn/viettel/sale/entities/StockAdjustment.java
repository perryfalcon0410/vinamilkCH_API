package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
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
    private Date adjustmentDate;
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

}
