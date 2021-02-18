package vn.viettel.core.dto.reservation;

public class ReservationSalonMainMenuResponseDTO {
    private Long id;

    private String slug;

    private String name;

    private Boolean isMySalon;

    public ReservationSalonMainMenuResponseDTO() {
    }

    public ReservationSalonMainMenuResponseDTO(Long id, String slug, String name, Boolean isMySalon) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.isMySalon = isMySalon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsMySalon() {
        return isMySalon;
    }

    public void setIsMySalon(Boolean mySalon) {
        isMySalon = mySalon;
    }
}
