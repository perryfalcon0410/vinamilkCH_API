package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    List<CustomerTypeDTO> getAll();

    /*
    Lấy danh sách loại khách hàng theo ds id
     */
    List<CustomerTypeDTO> findByIds(List<Long> customerTypeIds);

    CustomerTypeDTO getCusTypeByShopId(long shopId);
    CustomerTypeDTO getCustomerTypeDefaut();
    CustomerTypeDTO findByCustomerTypeId(Long customerTypeId);

    Long getWarehouseTypeIdByCustomer(Long id);
}
