package vn.viettel.core.dto.payment;

public class PaymentProductDetailDTO {
    private Long productId;

    private String name;

    private Double price;

    private Long amount;

    private Double cost;

    public PaymentProductDetailDTO() {
    }

    public PaymentProductDetailDTO(Long productId, String name, Double price, Long amount, Double cost) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.cost = cost;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
