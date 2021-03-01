package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "salon_points")
@AttributeOverride(name = "id", column = @Column(name = "salon_point_id"))
public class SalonPoint extends BaseEntity {

    @Column(name = "salon_id", nullable = false)
    private Long salonId;

    @Column(name = "name")
    private String name;

    @Column(name = "type", nullable = false)
    private Long type;

    @Column(name = "point")
    private Double point;

    @Column(name = "cost_base")
    private Double costBase;

    @Column(name = "enable")
    private Boolean enable;

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

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Double getCostBase() {
        return costBase;
    }

    public void setCostBase(Double costBase) {
        this.costBase = costBase;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
