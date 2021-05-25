package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    List<CustomerTypeDTO> getAll();
    CustomerTypeDTO findById(Long id);

    CustomerTypeDTO getCusTypeByShopId(long shopId);
    CustomerTypeDTO getCustomerTypeDefaut();
    CustomerTypeDTO findByCustomerTypeId(Long customerTypeId);

    Long getWarehouseTypeIdByCustomer(Long id);
}
