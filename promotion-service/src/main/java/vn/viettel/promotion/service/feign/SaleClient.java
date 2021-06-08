package vn.viettel.promotion.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import vn.viettel.promotion.service.dto.PriceDTO;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleClient {
    @GetMapping(value = {"/api/v1/sales/price/{productId}"})
    Response<PriceDTO> getPriceByPrIdV1(@PathVariable Long productId);
}
