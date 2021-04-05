package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.service.dto.*;

import java.util.Date;
import java.util.List;

public interface CustomerService extends BaseService {
    Response<Page<CustomerDTO>> index(String searchKeywords, Date fromDate, Date toDate, Long customerTypeId, Long status, Long genderId
            , Long areaId, String phone, String idNo, Pageable pageable);

    Response<CustomerDTO> create(CustomerRequest customerRequest, Long userId);

    Response<CustomerDTO> getCustomerById(Long id);

    Response<CustomerDTO> update(CustomerRequest request, Long userId);

    Response<CustomerDTO> delete(CustomerRequest request, Long userId);

    Response<List<Response<CustomerDTO>>> deleteBulk(CustomerBulkDeleteRequest request, Long userId);

    Response<Customer> getByIdAndType(Long id, Long type);
}

