package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
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

    @Column(name = "product_slug")
    private String productSlug;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "unit")
    private String unit;

    @Column(name = "is_top")
    private Integer is_Top;

    @Column(name = "tax")
    private Float tax;

    @Column(name = "convfact")
    private Integer convfact;

    @Column(name = "grossweight")
    private Float grossweight;

    @Column(name = "netweight")
    private Float netweight;

    @Column(name = "expiry_type")
    private Integer expiryType;

    @Column(name = "expiry_date")
    private Integer expiryDate;

    @Column(name = "height")
    private Float height;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "lenght")
    private Float lenght;

    @Column(name = "status")
    private Integer status;

}
