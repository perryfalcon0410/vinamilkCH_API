package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface CategoryDataClient {
    @GetMapping("api/sale/categorydata/findById/{id}")
    CategoryData getCategoryDataById(@PathVariable("id") Long id);

    @GetMapping("api/sale/categorydata/findAllByCategoryGroupCode")
    Response<List<CategoryData>> getAllByCategoryGroupCode(@RequestParam(value = "categoryGroupCode") String categoryGroupCode);
}
