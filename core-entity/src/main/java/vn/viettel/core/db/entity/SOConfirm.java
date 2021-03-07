package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "so_confirm")
public class SOConfirm extends BaseEntity{
    @Column(name = "po_confirm_id")
    private Long poConfirmId;

    @Column(name = "so_no")
    private String soNo;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private Float productPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_total")
    private Float priceTotal;

    @Column(name = "unit")
    private String unit;

    @Column(name = "is_free_item")
    private Integer isFreeItem;

}
