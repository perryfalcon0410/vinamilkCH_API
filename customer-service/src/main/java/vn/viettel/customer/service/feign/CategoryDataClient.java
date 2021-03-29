package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "authorization-service")
public interface CategoryDataClient {
    @GetMapping("api/sale/findById/{id}")
    CategoryData getCategoryDataById(@PathVariable("id") long id);
}
