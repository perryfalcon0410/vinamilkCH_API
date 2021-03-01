package vn.viettel.core.db.entity;


import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "order_product_details")
@AttributeOverride(name = "id", column = @Column(name = "order_detail_id"))
public class OrderProductDetail extends BaseEntity {

    @Column(name = "reception_id")
    private Long receptionId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "price")
    private Double price;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
