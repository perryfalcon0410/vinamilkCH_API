package vn.viettel.core.db.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_total")
public class StockTotal extends BaseEntity{

    @Column(name = "warehouse_id")
    private Long wareHouseId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "avaliable_quantity")
    private Integer avaliableQuantity;

    @Column(name = "status")
    private Integer status;

    @Column(name = "discription")
    private String discription;
}
