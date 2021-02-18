package vn.viettel.core.dto.salon;

public class SalonNameResponseDTO {
    private Long salonId;
    private String salonName;

    public SalonNameResponseDTO() {
    }

    public SalonNameResponseDTO(Long salonId, String salonName) {
        this.salonId = salonId;
        this.salonName = salonName;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }
}
