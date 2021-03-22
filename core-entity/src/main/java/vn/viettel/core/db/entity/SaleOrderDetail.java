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
    @Column(name = "NOTE")
    private String note;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DELETED_BY")
    private Long deletedBy;
}
