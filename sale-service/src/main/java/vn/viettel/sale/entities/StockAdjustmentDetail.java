package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_ADJUSTMENT_DETAIL")
public class StockAdjustmentDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "ADJUSTMENT_ID")
    private Long adjustmentId;
    @Column(name = "ADJUSTMENT_DATE")
    private LocalDateTime adjustmentDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Double price;

}