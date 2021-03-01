package vn.viettel.core.service.impl;

import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.service.feign.CompanyClient;
import vn.viettel.core.service.lazy.CompanyLazyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyLazyServiceImpl implements CompanyLazyService {

    @Autowired
    CompanyClient companyClient;

    @Override
    public List<CompanyFeatureListDTO> getAvailableCompanyFeatureList(Long companyId) {
        return companyClient.feignGetAvailableFeatureListByCompanyId(companyId);
    }
}
