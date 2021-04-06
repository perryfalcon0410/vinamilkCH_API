package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.service.dto.*;

import java.util.Date;

public interface CustomerService extends BaseService {
    Response<Page<CustomerDTO>> index(String searchKeywords, Date fromDate, Date toDate, Long customerTypeId, Long status, Long genderId
            , Long areaId, String phone, String idNo, Pageable pageable);

    Response<CustomerDTO> create(CustomerRequest customerRequest, Long userId);

    Response<CustomerDTO> getCustomerById(Long id);

    Response<Customer> getCustomerByPhone(String phone);

    Response<CustomerDTO> update(CustomerRequest request, Long userId);

    Response<Page<CustomerDTO>> findAllCustomer(Pageable pageable);
}

