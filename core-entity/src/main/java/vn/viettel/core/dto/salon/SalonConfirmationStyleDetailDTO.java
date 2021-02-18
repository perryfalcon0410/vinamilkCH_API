package vn.viettel.core.dto.salon;

public class SalonConfirmationStyleDetailDTO {
    private Long id;

    private String name;

    private Double cost;

    private Long salonStyleTypeId;

    private Double point;

    public SalonConfirmationStyleDetailDTO() {
    }

    public SalonConfirmationStyleDetailDTO(Long id, String name, Double cost, Long salonStyleTypeId, Double point) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.salonStyleTypeId = salonStyleTypeId;
        this.point = point;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
