package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerTypeClient {
    @GetMapping("/api/v1/customers/customer-types/shop-id/{shopId}")
    CustomerTypeDTO getCusTypeIdByShopIdV1(@PathVariable("shopId") Long shopId);

    @GetMapping("/api/v1/customers/customer-types/default")
    Response<CustomerTypeDTO> getCustomerTypeDefaultV1();

    @GetMapping("/api/v1/customers/customer-types/warehouse-type/shop/{shopId}")
    Long getWarehouseTypeByShopId( @PathVariable Long shopId);

    @GetMapping("/api/v1/customers//warehouse-type/customer/{customerId}")
    CustomerTypeDTO getCusTypeByCustomerIdV1(@PathVariable("shopId") Long customerId);
}
