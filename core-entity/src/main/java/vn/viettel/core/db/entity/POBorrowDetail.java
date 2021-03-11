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
@Table(name = "po_borrow_detail")
public class POBorrowDetail extends BaseEntity{
    @Column(name = "po_borrow_id")
    private Long poBorrowId;

    @Column(name = "po_borrow_detail_number")
    private String poBorrowDetailNumber;

    @Column(name = "product_id")
    private Long productId;

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

    @Column(name = "is_free_item")
    private Integer isFreeItem;
}
