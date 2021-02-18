package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;
import java.util.List;

public class SalonMenuResponseDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private Long salonId;

    private String name;

    private Long type;

    private String typeName;

    private Integer time;

    private String description;

    private Double cost;

    private Short optional;

    private String tagName;

    private List<String> tagNameList;

    public SalonMenuResponseDTO() {
    }

    public SalonMenuResponseDTO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Long salonId, String name, Long type, String typeName, Integer time, String description, Double cost, Short optional, String tagName, List<String> tagNameList) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.salonId = salonId;
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.time = time;
        this.description = description;
        this.cost = cost;
        this.optional = optional;
        this.tagName = tagName;
        this.tagNameList = tagNameList;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Short getOptional() {
        return optional;
    }

    public void setOptional(Short optional) {
        this.optional = optional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<String> getTagNameList() {
        return tagNameList;
    }

    public void setTagNameList(List<String> tagNameList) {
        this.tagNameList = tagNameList;
    }
}
