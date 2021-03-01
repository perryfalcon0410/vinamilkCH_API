package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getIstop() {
        return istop;
    }

    public void setIstop(Integer istop) {
        this.istop = istop;
    }

    public Float getTax() {
        return tax;
    }

    public void setTax(Float tax) {
        this.tax = tax;
    }

    public Integer getConvfact() {
        return convfact;
    }

    public void setConvfact(Integer convfact) {
        this.convfact = convfact;
    }

    public Float getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(Float grossweight) {
        this.grossweight = grossweight;
    }

    public Float getNetweight() {
        return netweight;
    }

    public void setNetweight(Float netweight) {
        this.netweight = netweight;
    }

    public Integer getExpirytype() {
        return expirytype;
    }

    public void setExpirytype(Integer expirytype) {
        this.expirytype = expirytype;
    }

    public Integer getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(Integer expirydate) {
        this.expirydate = expirydate;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getLenght() {
        return lenght;
    }

    public void setLenght(Float lenght) {
        this.lenght = lenght;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
