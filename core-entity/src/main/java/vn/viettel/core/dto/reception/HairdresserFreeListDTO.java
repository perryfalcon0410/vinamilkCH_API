package vn.viettel.core.dto.reception;

import vn.viettel.core.dto.salon.SalonHairdresserResponseDTO;

import java.util.List;

public class HairdresserFreeListDTO {
    private List<SalonHairdresserResponseDTO> freeHairdressers;

    private List<SalonHairdresserResponseDTO> mostFreeOnes;

    public List<SalonHairdresserResponseDTO> getFreeHairdressers() {
        return freeHairdressers;
    }

    public void setFreeHairdressers(List<SalonHairdresserResponseDTO> freeHairdressers) {
        this.freeHairdressers = freeHairdressers;
    }

    public List<SalonHairdresserResponseDTO> getMostFreeOnes() {
        return mostFreeOnes;
    }

    public void setMostFreeOnes(List<SalonHairdresserResponseDTO> mostFreeOnes) {
        this.mostFreeOnes = mostFreeOnes;
    }
}
