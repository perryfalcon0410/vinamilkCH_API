package vn.viettel.core.db.entity.stock;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "STOCK_TOTAL")
public class StockTotal extends BaseEntity {

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
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
