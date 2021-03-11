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
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "slug")
    private String productSlug;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "unit")
    private String unit;

    @Column(name = "istop")
    private Integer isTop;

    @Column(name = "tax")
    private Float tax;

    @Column(name = "convfact")
    private Integer convfact;

    @Column(name = "grossweight")
    private Float grossweight;

    @Column(name = "netweight")
    private Float netweight;

    @Column(name = "expirytype")
    private Integer expiryType;

    @Column(name = "expirydate")
    private Integer expiryDate;

    @Column(name = "height")
    private Float height;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "lenght")
    private Float lenght;

    @Column(name = "status")
    private Integer status;

    @Column(name = "product_type_id")
    private long proTypeId;

}
