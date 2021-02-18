package vn.viettel.core.dto.company;

import java.util.List;

public class CompanyFeatureListDTO {
    private Long featureId;

    private String name;

    private Boolean status;

    private Long parentId;

    private List<CompanyFeatureListDTO> childs;

    public CompanyFeatureListDTO() {
    }

    public CompanyFeatureListDTO(Long featureId, String name, Boolean status, Long parentId, List<CompanyFeatureListDTO> childs) {
        this.featureId = featureId;
        this.name = name;
        this.status = status;
        this.parentId = parentId;
        this.childs = childs;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<CompanyFeatureListDTO> getChilds() {
        return childs;
    }

    public void setChilds(List<CompanyFeatureListDTO> childs) {
        this.childs = childs;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
