package vn.viettel.sale.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_TOTAL")
public class StockTotal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WARE_HOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "STATUS")
    private Integer status;
}
