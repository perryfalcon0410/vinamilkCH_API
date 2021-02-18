package vn.viettel.core.service.lazy;

import vn.viettel.core.dto.company.CompanyFeatureListDTO;

import java.util.List;

public interface CompanyLazyService {
    List<CompanyFeatureListDTO> getAvailableCompanyFeatureList(Long companyId);
}
