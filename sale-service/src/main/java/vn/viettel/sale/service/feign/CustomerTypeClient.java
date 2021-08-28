package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "customer-service")
public interface CustomerTypeClient {

    @GetMapping("/api/v1/customers/customer-types/shop-id/{shopId}")
    CustomerTypeDTO getCusTypeByShopIdV1(@PathVariable("shopId") Long shopId);

    @GetMapping("/api/v1/customers/customer-types/warehouse-type/shop/{shopId}")
    Long getWarehouseTypeByShopId( @PathVariable Long shopId);

    @GetMapping("/api/v1/customers/customer-types/{id}")
    CustomerTypeDTO getCusTypeById(@PathVariable Long id);

    @GetMapping("/api/v1/customers/customer-types/getbywarehouse")
    List<CustomerTypeDTO> getCusTypeByWarehouse(@RequestParam Long warehouseId);

    @GetMapping("/api/v1/customers/customer-types/sale-order")
    CustomerTypeDTO getCustomerTypeForSale(@RequestParam Long customerId, @RequestParam Long shopId);

}
