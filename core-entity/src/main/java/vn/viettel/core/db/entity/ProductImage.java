package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "productimage")
public class ProductImage extends BaseEntity {

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "url")
    private String url;

    @Column(name = "status")
    private Integer status;

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}