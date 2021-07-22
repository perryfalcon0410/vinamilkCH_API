package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    List<CustomerTypeDTO> getAll();
    List<CustomerTypeDTO> getAllToCustomer();

    /*
    Lấy danh sách loại khách hàng theo ds id
     */
    List<CustomerTypeDTO> findByIds(List<Long> customerTypeIds);

    /*
    Lấy id loại kho
    	Trường hợp shop không có KH mặc định:
	Lấy theo loại KH mặc định: Lấy ra danh sách các loại khách hàng mà có POS_MODIFY_CUS = 1 , STATUS = 1,
	order ASC theo mã => lấy ra WAREHOUSE_TYPE_ID đầu tiên --> map sang WAREHOUSE_TYPE để lấy mã kho
    */
    Long getWarehouseTypeByShopId(Long shopId);

    CustomerTypeDTO getCusTypeByShopId(long shopId);

    CustomerTypeDTO getCustomerTypeDefaut();

    CustomerTypeDTO getCusTypeByCustomerId(long customerId);
}
