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
@Table(name = "PO_ADJUSTED_DETAILS")
public class POAdjustedDetail extends BaseEntity{
    @Column(name = "PO_ADJUSTED_ID")
    private Long poAdjustedId;

    @Column(name = "PO_ADJUSTED_DETAIL_NUMBER")
    private String poAdjustedDetailNumber;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_PRICE")
    private Float productPrice;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRICE_TOTAL")
    private Float priceTotal;

    @Column(name = "IS_FREE_ITEM")
    private Integer isFreeItem;
    @Column(name = "UNIT")
    private String unit;
}
