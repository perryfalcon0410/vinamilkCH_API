package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface CusPromotionClient {
    @GetMapping("/api/v1/cus-promotion/cus-type/{programId}")
    Response<List<Long>> getCusTypeByPromotionId(@PathVariable Long programId);

    @GetMapping("/api/v1/cus-promotion/member-card/{programId}")
    Response<List<Long>> getMemberCardByPromotionId(@PathVariable Long programId);

    @GetMapping("/api/v1/cus-promotion/cus-loyal/{programId}")
    Response<List<Long>> getCusLoyalByPromotionId(@PathVariable Long programId);

    @GetMapping("/api/v1/cus-promotion/cus-card/{programId}")
    Response<List<Long>> getCusCardByPromotionId(@PathVariable Long programId);
}
