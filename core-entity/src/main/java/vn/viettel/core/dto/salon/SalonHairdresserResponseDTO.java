package vn.viettel.core.dto.salon;

public class SalonHairdresserResponseDTO {

    private Long id;

    private String name;

    private String privilegeName;

    private String description;

    private Double beauticianCost;

    private String photoUrl;

    public SalonHairdresserResponseDTO() {

    }

    public SalonHairdresserResponseDTO(Long id, String name, String privilegeName, String description, Double beauticianCost, String photoUrl) {
        this.id = id;
        this.name = name;
        this.privilegeName = privilegeName;
        this.description = description;
        this.beauticianCost = beauticianCost;
        this.photoUrl = photoUrl;
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

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBeauticianCost() {
        return beauticianCost;
    }

    public void setBeauticianCost(Double beauticianCost) {
        this.beauticianCost = beauticianCost;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
