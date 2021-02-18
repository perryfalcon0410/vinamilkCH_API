package vn.viettel.authorization.service.feign;

import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.company.CompanyDTO;
import vn.viettel.core.db.entity.Company;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClientAuthenticate(name = "company-service")
public interface CompanyClient {

    @GetMapping("/get-company-by-id-and-user-id")
    Response<CompanyDTO> getCompanyByIdAndUserId(@RequestParam("companyId") Long companyId,
                                                 @RequestParam("userId") Long userId);

    @GetMapping("/get-company-by-company-id")
    Response<CompanyDTO> getCompanyByCompanyId(@RequestParam("companyId") Long companyId);

    @GetMapping("/get-company-by-slug")
    public Company getCompanyBySlug(@RequestParam("companySlug") String slug);

    @GetMapping("/feature/feignGetAvailableFeatureListByCompanyId")
    public List<CompanyFeatureListDTO> feignGetAvailableFeatureListByCompanyId(
            @RequestParam("companyId") Long companyId);

    @GetMapping("/get-company-by-id")
    public Company getCompanyById(@RequestParam("companyId") Long companyId);
}
