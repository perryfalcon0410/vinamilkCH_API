package vn.viettel.gateway.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "report-service")
public interface ReportClient {

    @GetMapping("/reports/url")
    String getURLValue();
}
