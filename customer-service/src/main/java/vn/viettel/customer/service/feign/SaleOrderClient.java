package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.SaleOrderDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleOrderClient {

    @GetMapping(value = { "api/v1/sales/sale-orders/top-five-products"})
    Response<List<String>> getTopFiveFavoriteProductsV1(@RequestParam Long customerId);
}
