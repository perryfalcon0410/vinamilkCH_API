package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonStyleWithTypeAndGroup {
    private Long salonStyleTypeId;

    private String salonStyleTypeName;

    private Double cost;

    private List<SalonStyleWithGroup> groups;

    public SalonStyleWithTypeAndGroup() {
        //groups = new ArrayList<>();
    }

    public SalonStyleWithTypeAndGroup(Long salonStyleTypeId, String salonStyleTypeName, List<SalonStyleWithGroup> groups, Double cost) {
        this.salonStyleTypeId = salonStyleTypeId;
        this.salonStyleTypeName = salonStyleTypeName;
        this.groups = groups;
        this.cost = cost;
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

    public List<SalonStyleWithGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<SalonStyleWithGroup> groups) {
        this.groups = groups;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
