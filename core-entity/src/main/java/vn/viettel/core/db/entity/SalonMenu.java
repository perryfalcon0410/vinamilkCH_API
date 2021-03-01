package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_menus")
@AttributeOverride(name = "id", column = @Column(name = "salon_menu_id"))
public class SalonMenu extends BaseEntity {

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private Long type;

    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "optional", nullable = false)
    private Short optional;

    @Column(name = "tax", nullable = false)
    private Double tax;

    @Column(name = "display", nullable = false)
    private Boolean display;

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

    public Double getTax() { return tax; }

    public void setTax(Double tax) { this.tax = tax; }

    public Boolean getDisplay() { return display; }

    public void setDisplay(Boolean display) { this.display = display; }
}
