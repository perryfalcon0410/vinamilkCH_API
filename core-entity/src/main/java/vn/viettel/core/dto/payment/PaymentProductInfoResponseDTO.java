package vn.viettel.core.dto.payment;

import vn.viettel.core.dto.company.CompanyProductResponseDTO;
import vn.viettel.core.dto.salon.SalonConfirmationDetailDTO;

import java.util.List;

public class PaymentProductInfoResponseDTO {

    private List<CompanyProductResponseDTO> categories;

    private SalonConfirmationDetailDTO salonConfirmationDetailDTO;

    public PaymentProductInfoResponseDTO() {
    }

    public PaymentProductInfoResponseDTO(List<CompanyProductResponseDTO> categories, SalonConfirmationDetailDTO salonConfirmationDetailDTO) {
        this.categories = categories;
        this.salonConfirmationDetailDTO = salonConfirmationDetailDTO;
    }

    public List<CompanyProductResponseDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CompanyProductResponseDTO> categories) {
        this.categories = categories;
    }

    public SalonConfirmationDetailDTO getSalonConfirmationDetailDTO() {
        return salonConfirmationDetailDTO;
    }

    public void setSalonConfirmationDetailDTO(SalonConfirmationDetailDTO salonConfirmationDetailDTO) {
        this.salonConfirmationDetailDTO = salonConfirmationDetailDTO;
    }
}
