package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SALE_ORDER_DETAILS")
public class SaleOrderDetail extends BaseEntity{
    @Column(name = "SALE_ORDER_ID")
    private long saleOrderId;
    @Column(name = "PRODUCT_ID")
    private long productId;
    @Column(name = "QUANTITY")
    private int quantity;
    @Column(name = "QUANTITY_IN_STOCK")
    private int quantityInStock;
    @Column(name = "CREATED_BY")
    private long created_by;
    @Column(name = "UPDATED_BY")
    private long updated_by;
    @Column(name = "DELETED_BY")
    private long deleted_by;
}
