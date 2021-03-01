package vn.viettel.core.db.entity;


import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product")
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class CompanyProduct extends BaseEntity {

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
