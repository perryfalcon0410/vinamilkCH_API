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
    @GetMapping("api/v1/sales/sale-orders/last-sale-order/{id}")
    Response<SaleOrderDTO> GetLastSaleOrderByCustomerIdV1(@PathVariable("id") Long id);

    @GetMapping("api/v1/sales/sale-orders/total-bill-for-month")
    Response<Double> getTotalBillForTheMonthByCustomerIdV1(@RequestParam Long customerId,
                                                         @RequestParam(value = "lastOrderDate", required = false) LocalDate lastOrderDate);

    @GetMapping(value = { "api/v1/sales/sale-orders/top-five-products"})
    Response<List<String>> getTopFiveFavoriteProductsV1(@RequestParam Long customerId);
}
