package vn.viettel.core.dto.waiting;

import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;
import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.dto.salon.SalonConfirmationMenuDetailDTO;
import vn.viettel.core.dto.salon.SalonConfirmationStyleDetailDTO;

import java.util.List;

public class WaitingInterchangeBookingDetail {

    private List<SalonConfirmationMenuDetailDTO> mainMenuSelectionList;

    private List<SalonConfirmationMenuDetailDTO> recommendMenuSelectionList;

    private List<SalonConfirmationHairdresserDetailDTO> subHairdresserSelectionList;

    private List<SalonConfirmationStyleDetailDTO> salonHairStyleSelectionList;

    private List<SalonConfirmationStyleDetailDTO> salonDesignStyleSelectionList;

    private SalonConfirmationDetailDTO salonConfirmationData;

    public WaitingInterchangeBookingDetail() {
    }

    public List<SalonConfirmationMenuDetailDTO> getMainMenuSelectionList() {
        return mainMenuSelectionList;
    }

    public void setMainMenuSelectionList(List<SalonConfirmationMenuDetailDTO> mainMenuSelectionList) {
        this.mainMenuSelectionList = mainMenuSelectionList;
    }

    public List<SalonConfirmationMenuDetailDTO> getRecommendMenuSelectionList() {
        return recommendMenuSelectionList;
    }

    public void setRecommendMenuSelectionList(List<SalonConfirmationMenuDetailDTO> recommendMenuSelectionList) {
        this.recommendMenuSelectionList = recommendMenuSelectionList;
    }

    public List<SalonConfirmationHairdresserDetailDTO> getSubHairdresserSelectionList() {
        return subHairdresserSelectionList;
    }

    public void setSubHairdresserSelectionList(List<SalonConfirmationHairdresserDetailDTO> subHairdresserSelectionList) {
        this.subHairdresserSelectionList = subHairdresserSelectionList;
    }

    public List<SalonConfirmationStyleDetailDTO> getSalonHairStyleSelectionList() {
        return salonHairStyleSelectionList;
    }

    public void setSalonHairStyleSelectionList(List<SalonConfirmationStyleDetailDTO> salonHairStyleSelectionList) {
        this.salonHairStyleSelectionList = salonHairStyleSelectionList;
    }

    public List<SalonConfirmationStyleDetailDTO> getSalonDesignStyleSelectionList() {
        return salonDesignStyleSelectionList;
    }

    public void setSalonDesignStyleSelectionList(List<SalonConfirmationStyleDetailDTO> salonDesignStyleSelectionList) {
        this.salonDesignStyleSelectionList = salonDesignStyleSelectionList;
    }

    public SalonConfirmationDetailDTO getSalonConfirmationData() {
        return salonConfirmationData;
    }

    public void setSalonConfirmationData(SalonConfirmationDetailDTO salonConfirmationData) {
        this.salonConfirmationData = salonConfirmationData;
    }
}
