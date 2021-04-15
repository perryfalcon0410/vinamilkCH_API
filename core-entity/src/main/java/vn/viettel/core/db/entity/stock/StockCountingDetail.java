package vn.viettel.core.db.entity.stock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_COUNTING_DETAIL")
public class StockCountingDetail extends BaseEntity {
    @Column(name = "STOCK_COUNTING_ID")
    private Long stockCountingId;
    @Column(name = "COUNTING_DATE")
    private Date countingDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;
}
