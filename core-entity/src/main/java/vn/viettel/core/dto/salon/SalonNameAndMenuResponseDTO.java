package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonNameAndMenuResponseDTO {
    private List<SalonNameResponseDTO> salonNameResponseDTO;
    private List<SalonMenuNameResponseDTO> salonMenuNameResponseDTO;

    public SalonNameAndMenuResponseDTO() {
    }

    public SalonNameAndMenuResponseDTO(List<SalonNameResponseDTO> salonNameResponseDTO, List<SalonMenuNameResponseDTO> salonMenuNameResponseDTO) {
        this.salonNameResponseDTO = salonNameResponseDTO;
        this.salonMenuNameResponseDTO = salonMenuNameResponseDTO;
    }

    public List<SalonNameResponseDTO> getSalonNameResponseDTO() {
        return salonNameResponseDTO;
    }

    public void setSalonNameResponseDTO(List<SalonNameResponseDTO> salonNameResponseDTO) {
        this.salonNameResponseDTO = salonNameResponseDTO;
    }

    public List<SalonMenuNameResponseDTO> getSalonMenuNameResponseDTO() {
        return salonMenuNameResponseDTO;
    }

    public void setSalonMenuNameResponseDTO(List<SalonMenuNameResponseDTO> salonMenuNameResponseDTO) {
        this.salonMenuNameResponseDTO = salonMenuNameResponseDTO;
    }
}
