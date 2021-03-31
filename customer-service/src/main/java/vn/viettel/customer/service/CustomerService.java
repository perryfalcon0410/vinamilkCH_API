package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerCreateRequest;
import vn.viettel.customer.messaging.CustomerDeleteRequest;
import vn.viettel.customer.messaging.CustomerUpdateRequest;
import vn.viettel.customer.service.dto.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomerService extends BaseService {
    Response<Page<CustomerDTO>> index(String searchKeywords, Date fromDate, Date toDate, Long customerTypeId, Long status, Long genderId, Long areaId, Pageable pageable);

    Response<Customer> create(CustomerCreateRequest customerCreateRequest, Long userId);

    Response<CustomerDTO> edit(Long id);

    Response<Customer> getCustomerById(Long id);

    Response<CustomerDTO> update(CustomerUpdateRequest request, Long id, Long userId);

    Response<CustomerDTO> delete(CustomerDeleteRequest request, Long userId);

    Response<List<Response<CustomerDTO>>> deleteBulk(CustomerBulkDeleteRequest request, Long userId);

    Response<List<LocationDTO>> getAllLocationOfCustomers(Long shopId);
}

