package vn.viettel.core.service.feign;

import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "company-service")
public interface CompanyClient {
    @GetMapping("/feature/feignGetAvailableFeatureListByCompanyId")
    public List<CompanyFeatureListDTO> feignGetAvailableFeatureListByCompanyId(
            @RequestParam("companyId") Long companyId);
}
