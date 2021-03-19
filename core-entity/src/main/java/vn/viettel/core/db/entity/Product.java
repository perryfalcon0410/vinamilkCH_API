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

    @Column(name = "PARENT_PRODUCT_CODE")
    private String parentProductCode;

    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "UOM2")
    private String uom2;

    @Column(name = "CAT_ID")
    private Long catId;
    @Column(name = "SUB_CAT_ID")
    private Long subCatId;

    @Column(name = "BRAND_ID")
    private Long brandId;

    @Column(name = "FLAVOUR_ID")
    private Long flavourId;

    @Column(name = "PACKING_ID")
    private Long packingId;
    @Column(name = "PRODUCT_TYPE_ID")
    private Long productTypeId;

    @Column(name = "BAR_CODE")
    private String barCode;

    @Column(name = "CONVFACT")
    private Integer convfact;

    @Column(name = "GROSS_WEIGHT")
    private Float grossWeight;

    @Column(name = "NET_WEIGHT")
    private Float netWeight;

    @Column(name = "EXPIRY_TYPE")
    private Integer expiryType;

    @Column(name = "EXPIRY_NUM")
    private Integer expiryDate;

    @Column(name = "HEIGHT")
    private Float height;

    @Column(name = "WEIGHT")
    private Float weight;

    @Column(name = "LENGTH")
    private Float length;

    @Column(name = "STATUS")
    private Integer status;

}
