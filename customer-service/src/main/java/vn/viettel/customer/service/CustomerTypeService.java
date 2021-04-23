package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    Response<List<CustomerTypeDTO>> getAll();
    Response<CustomerTypeDTO> findById(Long id);
    CustomerTypeDTO getCusTypeByShopId(long shopId);
    Response<CustomerTypeDTO> getCustomerTypeDefaut();
    Response<CustomerTypeDTO> findByCustomerTypeId(Long customerTypeId);
}
