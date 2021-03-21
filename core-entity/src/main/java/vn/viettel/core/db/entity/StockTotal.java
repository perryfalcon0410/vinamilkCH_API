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
@Table(name = "STOCK_TOTALS")
public class StockTotal extends BaseEntity{

    @Column(name = "WARE_HOUSE_ID")
    private Long wareHouseId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "AVAILABLE_QUANTITY")
    private Integer availableQuantity;


    @Column(name = "DESCRIPTION")
    private String description;
}
