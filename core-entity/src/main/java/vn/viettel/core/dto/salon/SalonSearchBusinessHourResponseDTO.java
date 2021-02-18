package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonSearchBusinessHourResponseDTO {
    private Long salonId;
    private List<SalonBusinessHourResponseDTO> salonBusinessHour;

    public SalonSearchBusinessHourResponseDTO() {
    }

    public SalonSearchBusinessHourResponseDTO(Long salonId, List<SalonBusinessHourResponseDTO> salonBusinessHour) {
        this.salonId = salonId;
        this.salonBusinessHour = salonBusinessHour;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public List<SalonBusinessHourResponseDTO> getSalonBusinessHour() {
        return salonBusinessHour;
    }

    public void setSalonBusinessHour(List<SalonBusinessHourResponseDTO> salonBusinessHour) {
        this.salonBusinessHour = salonBusinessHour;
    }
}
