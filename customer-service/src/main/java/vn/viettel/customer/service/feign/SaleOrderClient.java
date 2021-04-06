package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleOrderClient {
    @GetMapping("/sale-order/last-sale-order/{id}")
    Response<SaleOrder> GetLastSaleOrderByCustomerId(@PathVariable("id") Long id);
}
