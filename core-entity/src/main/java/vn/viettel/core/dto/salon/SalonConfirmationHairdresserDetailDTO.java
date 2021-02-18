package vn.viettel.core.dto.salon;

public class SalonConfirmationHairdresserDetailDTO {
    private Long id;

    private String name;

    private Double beauticianCost;

    public SalonConfirmationHairdresserDetailDTO() {
    }

    public SalonConfirmationHairdresserDetailDTO(Long id, String name, Double beauticianCost) {
        this.id = id;
        this.name = name;
        this.beauticianCost = beauticianCost;
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

    public Double getBeauticianCost() {
        return beauticianCost;
    }

    public void setBeauticianCost(Double beauticianCost) {
        this.beauticianCost = beauticianCost;
    }
}
