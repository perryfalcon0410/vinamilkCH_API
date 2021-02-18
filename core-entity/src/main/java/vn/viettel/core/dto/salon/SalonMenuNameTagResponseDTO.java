package vn.viettel.core.dto.salon;

public class SalonMenuNameTagResponseDTO {

    private Integer salonId;
    private Integer salonMenuId;
    private String menuName;
    private String menuTag;
    private String description;
    private Double cost;
    private Integer time;
    private Short optional;
    private Double tax;
    private Boolean display;

    public String getMenuName() {
        return menuName;
    }

    public Integer getSalonId() { return salonId; }

    public void setSalonId(Integer salonId) { this.salonId = salonId; }

    public Integer getSalonMenuId() { return salonMenuId; }

    public void setSalonMenuId(Integer salonMenuId) { this.salonMenuId = salonMenuId; }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuTag() {
        return menuTag;
    }

    public void setMenuTag(String menuTag) {
        this.menuTag = menuTag;
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

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Short getOptional() {
        return optional;
    }

    public void setOptional(Short optional) {
        this.optional = optional;
    }

    public Double getTax() { return tax; }

    public void setTax(Double tax) { this.tax = tax; }

    public Boolean getDisplay() { return display; }

    public void setDisplay(Boolean display) { this.display = display; }
}
