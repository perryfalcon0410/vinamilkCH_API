package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonStylesResponseDTO {

    private Long id;

    private Long salonId;

    private String name;

    private String description;

    private String photoURL;

    private Double cost;

    private String tag;

    private List<String> tagList;

    private Long salonStyleTypeId;

    private String salonStyleTypeName;

    private Long salonStyleGroupId;

    private String salonStyleGroupName;

    private Double styleCost;

    public SalonStylesResponseDTO() {
    }

    public SalonStylesResponseDTO(Long id, Long salonId, String name, String description, String photoURL, Double cost, String tag, List<String> tagList, Long salonStyleTypeId, String salonStyleTypeName, Long salonStyleGroupId, String salonStyleGroupName, Double styleCost) {
        this.id = id;
        this.salonId = salonId;
        this.name = name;
        this.description = description;
        this.photoURL = photoURL;
        this.cost = cost;
        this.tag = tag;
        this.tagList = tagList;
        this.salonStyleTypeId = salonStyleTypeId;
        this.salonStyleTypeName = salonStyleTypeName;
        this.salonStyleGroupId = salonStyleGroupId;
        this.salonStyleGroupName = salonStyleGroupName;
        this.styleCost = styleCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public Long getSalonStyleTypeId() {
        return salonStyleTypeId;
    }

    public void setSalonStyleTypeId(Long salonStyleTypeId) {
        this.salonStyleTypeId = salonStyleTypeId;
    }

    public String getSalonStyleTypeName() {
        return salonStyleTypeName;
    }

    public void setSalonStyleTypeName(String salonStyleTypeName) {
        this.salonStyleTypeName = salonStyleTypeName;
    }

    public Long getSalonStyleGroupId() {
        return salonStyleGroupId;
    }

    public void setSalonStyleGroupId(Long salonStyleGroupId) {
        this.salonStyleGroupId = salonStyleGroupId;
    }

    public String getSalonStyleGroupName() {
        return salonStyleGroupName;
    }

    public void setSalonStyleGroupName(String salonStyleGroupName) {
        this.salonStyleGroupName = salonStyleGroupName;
    }

    public Double getStyleCost() {
        return styleCost;
    }

    public void setStyleCost(Double styleCost) {
        this.styleCost = styleCost;
    }
}
