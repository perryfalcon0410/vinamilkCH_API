package vn.viettel.core.dto.payment;

import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.dto.salon.SalonHairdresserResponseDTO;
import vn.viettel.core.dto.salon.SalonMenuTagsResponseDTO;
import vn.viettel.core.dto.waiting.ReceptionSubManagementResponseDTO;

import java.util.List;

public class PersonInChargeInfoResponseDTO {
    private Long bookingId;
    private Long salonId;
    private Long receptionId;
    private Long customerId;
    private Long nextBookingId;
    private List<SalonConfirmationHairdresserDetailDTO> salonHairdressers;
    private List<SalonMenuTagsResponseDTO> menus;


    private SalonHairdresserResponseDTO mainPersonInCharge;

    private List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails;



    public PersonInChargeInfoResponseDTO() {
    }

    public PersonInChargeInfoResponseDTO(Long bookingId, Long salonId, Long receptionId, Long customerId, Long nextBookingId, List<SalonConfirmationHairdresserDetailDTO> salonHairdressers, List<SalonMenuTagsResponseDTO> menus, SalonHairdresserResponseDTO mainPersonInCharge, List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails) {
        this.bookingId = bookingId;
        this.salonId = salonId;
        this.receptionId = receptionId;
        this.customerId = customerId;
        this.nextBookingId = nextBookingId;
        this.salonHairdressers = salonHairdressers;
        this.menus = menus;
        this.mainPersonInCharge = mainPersonInCharge;
        this.salonSubHairdresserDetails = salonSubHairdresserDetails;
    }

    public List<SalonConfirmationHairdresserDetailDTO> getSalonHairdressers() {
        return salonHairdressers;
    }

    public void setSalonHairdressers(List<SalonConfirmationHairdresserDetailDTO> salonHairdressers) {
        this.salonHairdressers = salonHairdressers;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public List<SalonMenuTagsResponseDTO> getMenus() {
        return menus;
    }

    public void setMenus(List<SalonMenuTagsResponseDTO> menus) {
        this.menus = menus;
    }

    public SalonHairdresserResponseDTO getMainPersonInCharge() {
        return mainPersonInCharge;
    }

    public void setMainPersonInCharge(SalonHairdresserResponseDTO mainPersonInCharge) {
        this.mainPersonInCharge = mainPersonInCharge;
    }

    public List<ReceptionSubManagementResponseDTO> getSalonSubHairdresserDetails() {
        return salonSubHairdresserDetails;
    }

    public void setSalonSubHairdresserDetails(List<ReceptionSubManagementResponseDTO> salonSubHairdresserDetails) {
        this.salonSubHairdresserDetails = salonSubHairdresserDetails;
    }


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getNextBookingId() {
        return nextBookingId;
    }

    public void setNextBookingId(Long nextBookingId) {
        this.nextBookingId = nextBookingId;
    }
}
