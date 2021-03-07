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
@Table(name = "po_adjused_detail")
public class POAdjustedDetail extends BaseEntity{
    @Column(name = "po_adjusted_id")
    private Long poAdjustedId;

    @Column(name = "po_license_detail_number")
    private String poLicenseDetailNumber;

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
