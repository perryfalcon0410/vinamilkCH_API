package vn.viettel.core.dto.company;

import java.util.List;

public class CompanyCategoryResponseDTO {
    private Long companyId;
    private List<CompanyProductResponseDTO> categories;

    public CompanyCategoryResponseDTO() {
    }

    public CompanyCategoryResponseDTO(Long companyId, List<CompanyProductResponseDTO> categories) {
        this.companyId = companyId;
        this.categories = categories;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<CompanyProductResponseDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CompanyProductResponseDTO> categories) {
        this.categories = categories;
    }
}
