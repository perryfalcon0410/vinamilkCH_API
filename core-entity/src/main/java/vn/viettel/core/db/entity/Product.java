package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRODUCTS")
public class Product extends BaseEntity {

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_PRICE_ID")
    private long productPriceId;

    @Column(name = "SLUG")
    private String slug;

    @Column(name = "BAR_CODE")
    private String barCode;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "IS_TOP")
    private Integer isTop;

    @Column(name = "TAX")
    private Float tax;

    @Column(name = "CONVFACT")
    private Integer convfact;

    @Column(name = "GROSS_WEIGHT")
    private Float grossWeight;

    @Column(name = "NET_WEIGHT")
    private Float netWeight;

    @Column(name = "EXPIRY_TYPE")
    private Integer expiryType;

    @Column(name = "EXPIRY_DATE")
    private Integer expiryDate;

    @Column(name = "HEIGHT")
    private Float height;

    @Column(name = "WEIGHT")
    private Float weight;

    @Column(name = "LENGTH")
    private Float length;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "PRODUCT_TYPE_ID")
    private Integer productTypeId;

}
