package vn.viettel.core.dto.payment;

public class DirectOrderDetailDTO {
    private Long id;

    private String name;

    private String description;

    private Long quantity;

    private Double cost;

    public DirectOrderDetailDTO() {
    }

    public DirectOrderDetailDTO(Long id, String name, String description, Long quantity, Double cost) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
