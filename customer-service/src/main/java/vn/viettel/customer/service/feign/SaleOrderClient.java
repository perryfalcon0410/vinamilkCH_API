package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.SaleOrderDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleOrderClient {
    @GetMapping("api/v1/sales/sale-orders/last-sale-order/{id}")
    Response<SaleOrderDTO> GetLastSaleOrderByCustomerId(@PathVariable("id") Long id);
}
