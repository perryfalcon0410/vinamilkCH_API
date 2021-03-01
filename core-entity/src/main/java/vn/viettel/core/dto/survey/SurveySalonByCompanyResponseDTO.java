package vn.viettel.core.dto.survey;

import vn.viettel.core.dto.company.CompanyListResponseDTO;

import java.util.List;

public class SurveySalonByCompanyResponseDTO {
    private List<CompanyListResponseDTO> companies;
    private List<SurveySalonResponseDTO> salonSurveyList;

    public List<CompanyListResponseDTO> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyListResponseDTO> companies) {
        this.companies = companies;
    }

    public List<SurveySalonResponseDTO> getSalonSurveyList() {
        return salonSurveyList;
    }

    public void setSalonSurveyList(List<SurveySalonResponseDTO> salonSurveyList) {
        this.salonSurveyList = salonSurveyList;
    }
}
