package vn.viettel.core.dto.company;

import vn.viettel.core.db.entity.CompanyProduct;

import java.util.List;

public class CompanyProductResponseDTO {
    private Long categoryId;
    private String categoryName;
    private List<CompanyProduct> products;

    public CompanyProductResponseDTO() {
    }

    public CompanyProductResponseDTO(Long categoryId, String categoryName, List<CompanyProduct> products) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.products = products;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<CompanyProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CompanyProduct> products) {
        this.products = products;
    }
}
