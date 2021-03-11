package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PO_PROMOTIONAL_DETAILS")
public class PoPromotionalDetail extends BaseEntity{
    @Column(name = "PO_PROMOTIONAL_ID")
    private Long poPromotionalId;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRODUCT_PRICE")
    private Float productPrice;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "SO_NO")
    private String soNo;
    @Column(name = "TOTAL_PRICE")
    private Float totalPrice;
}
