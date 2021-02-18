package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_styles")
@AttributeOverride(name = "id", column = @Column(name = "salon_styles_id"))
public class SalonStyles extends BaseEntity {
    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "photo_url")
    private String photoURL;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "salon_style_type_id")
    private Long salonStyleTypeId;

    @Column(name = "point")
    private Double point;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "display")
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

    public Long getSalonStyleTypeId() {
        return salonStyleTypeId;
    }

    public void setSalonStyleTypeId(Long salonStyleTypeId) {
        this.salonStyleTypeId = salonStyleTypeId;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }
}
