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

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "unit")
    private String unit;

    @Column(name = "istop")
    private Integer istop;

    @Column(name = "tax")
    private Float tax;

    @Column(name = "convfact")
    private Integer convfact;

    @Column(name = "grossweight")
    private Float grossweight;

    @Column(name = "netweight")
    private Float netweight;

    @Column(name = "expirytype")
    private Integer expirytype;

    @Column(name = "expirydate")
    private Integer expirydate;

    @Column(name = "height")
    private Float height;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "lenght")
    private Float lenght;

    @Column(name = "status")
    private Integer status;

}
