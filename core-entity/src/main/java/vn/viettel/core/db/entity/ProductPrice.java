package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productprice")
public class ProductPrice extends BaseEntity {

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "price")
    private Float price;

    @Column(name = "pricenottax")
    private Float pricenottax;

    @Column(name = "packageprice")
    private Float packageprice;

    @Column(name = "packagepricenottax")
    private Float packagepricenottax;

    @Column(name = "fromdate")
    private LocalDateTime fromdate;

    @Column(name = "todate")
    private LocalDateTime todate;

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPricenottax() {
        return pricenottax;
    }

    public void setPricenottax(Float pricenottax) {
        this.pricenottax = pricenottax;
    }

    public Float getPackageprice() {
        return packageprice;
    }

    public void setPackageprice(Float packageprice) {
        this.packageprice = packageprice;
    }

    public Float getPackagepricenottax() {
        return packagepricenottax;
    }

    public void setPackagepricenottax(Float packagepricenottax) {
        this.packagepricenottax = packagepricenottax;
    }

    public LocalDateTime getFromdate() {
        return fromdate;
    }

    public void setFromdate(LocalDateTime fromdate) {
        this.fromdate = fromdate;
    }

    public LocalDateTime getTodate() {
        return todate;
    }

    public void setTodate(LocalDateTime todate) {
        this.todate = todate;
    }
}
