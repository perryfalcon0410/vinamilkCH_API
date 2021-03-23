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
@Table(name = "STOCK_ADJUSTMENT_TRANS_DETAIL")
public class StockAdjustmentTransDetail extends BaseEntity {
    @Column(name = "TRANS_ID")
    private Long transId;
    @Column(name = "TRANS_DATE")
    private Date transDat;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;
    @Column(name = "ORIGINAL_QUANTITY")
    private Integer originalQuantity;
}