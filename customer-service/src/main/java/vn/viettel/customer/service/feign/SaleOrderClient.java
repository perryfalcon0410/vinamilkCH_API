package vn.viettel.customer.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.SaleOrderDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.time.LocalDateTime;

@Service
@FeignClientAuthenticate(name = "sale-service")
public interface SaleOrderClient {
    @GetMapping("api/v1/sales/sale-orders/last-sale-order/{id}")
    Response<SaleOrderDTO> GetLastSaleOrderByCustomerIdV1(@PathVariable("id") Long id);

    @GetMapping("api/v1/sales/sale-orders/total-bill-for-month")
    Response<Double> getTotalBillForTheMonthByCustomerId(@RequestParam Long customerId,
                                                         @RequestParam(value = "lastOrderDate", required = false) LocalDateTime lastOrderDate);
}
