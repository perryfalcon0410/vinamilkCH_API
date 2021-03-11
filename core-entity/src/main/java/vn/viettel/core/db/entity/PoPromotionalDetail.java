package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "po_promotional_detail")
public class PoPromotionalDetail extends BaseEntity{
    @Column(name = "po_promotional_id")
    private Long poPromotionalId;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "product_price")
    private Float productPrice;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "unit")
    private String unit;
    @Column(name = "so_no")
    private String soNo;
    @Column(name = "total_price")
    private Float totalPrice;
}
