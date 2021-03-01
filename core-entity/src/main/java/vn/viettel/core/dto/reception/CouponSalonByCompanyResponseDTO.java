package vn.viettel.core.dto.reception;

import vn.viettel.core.dto.company.CompanyListResponseDTO;
import vn.viettel.core.dto.salon.SalonCouponResponseDTO;

import java.util.List;

public class CouponSalonByCompanyResponseDTO {
    private List<CompanyListResponseDTO> companies;
    private List<SalonCouponResponseDTO> salonCouponList;

    public List<CompanyListResponseDTO> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyListResponseDTO> companies) {
        this.companies = companies;
    }

    public List<SalonCouponResponseDTO> getSalonCouponList() {
        return salonCouponList;
    }

    public void setSalonCouponList(List<SalonCouponResponseDTO> salonCouponList) {
        this.salonCouponList = salonCouponList;
    }
}
