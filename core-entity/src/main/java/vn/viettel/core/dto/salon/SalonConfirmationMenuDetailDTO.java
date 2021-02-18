package vn.viettel.core.dto.salon;

public class SalonConfirmationMenuDetailDTO {
    private Long salonMenuId;

    private String name;

    private Double cost;

    private Short optional;

    private Integer time;

    public SalonConfirmationMenuDetailDTO() {
    }

    public SalonConfirmationMenuDetailDTO(Long salonMenuId, String name, Double cost, Short optional, Integer time) {
        this.salonMenuId = salonMenuId;
        this.name = name;
        this.cost = cost;
        this.optional = optional;
        this.time = time;
    }

    public Long getSalonMenuId() {
        return salonMenuId;
    }

    public void setSalonMenuId(Long salonMenuId) {
        this.salonMenuId = salonMenuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
