package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonStyleWithTag {
    private Long id;

    private Long salonId;

    private String name;

    private String description;

    private String photoURL;

    private Double cost;

    private String tag;

    private List<String> tagList;

    public SalonStyleWithTag() {
    }

    public SalonStyleWithTag(Long id, Long salonId, String name, String description, String photoURL, Double cost, String tag, List<String> tagList) {
        this.id = id;
        this.salonId = salonId;
        this.name = name;
        this.description = description;
        this.photoURL = photoURL;
        this.cost = cost;
        this.tag = tag;
        this.tagList = tagList;
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

    public boolean isPracticalNull() {
        return this.id == null && this.salonId == null && this.name == null && this.description == null
                && this.photoURL == null && this.cost == null && this.tag == null && this.tagList == null;
    }
}
