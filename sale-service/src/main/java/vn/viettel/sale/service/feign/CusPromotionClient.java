package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface CusPromotionClient {
    @GetMapping("/api/v1/promotions/promotion-cust-attr/{programId}")
    Response<List<Long>> findCusCardPromotion(@PathVariable Long programId, @RequestParam Long objectType);

}
