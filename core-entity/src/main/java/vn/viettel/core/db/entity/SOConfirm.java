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
@Table(name = "SO_CONFIRMS")
public class SOConfirm extends BaseEntity{
    @Column(name = "PO_CONFIRM_ID")
    private Long poConfirmId;
    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "SO_NO")
    private String soNo;

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

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "IS_FREE_ITEM")
    private Integer isFreeItem;
}
