package vn.viettel.core.dto.salon;

public class SalonPointDetailResponseDTO {
    private Long salonId;

    private String name;

    private String description;

    private Long type;

    private Double point;

    private Double costBase;

    private Boolean enable;

    public SalonPointDetailResponseDTO() {
    }

    public SalonPointDetailResponseDTO(Long salonId, String name, String description, Long type, Double point, Double costBase, Boolean enable) {
        this.salonId = salonId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.point = point;
        this.costBase = costBase;
        this.enable = enable;
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
