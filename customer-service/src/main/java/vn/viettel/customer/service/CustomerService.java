package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.service.dto.*;

import java.util.List;

public interface CustomerService extends BaseService {

    Response<CustomerDTO> create(CustomerRequest customerRequest, Long userId, Long shopId);

    Response<CustomerDTO> getCustomerById(Long id);

    Response<CustomerDTO> getCustomerByPhone(String phone);

    Response<CustomerDTO> update(CustomerRequest request, Long userId);

    Response<Page<CustomerDTO>> find(CustomerFilter filter, Pageable pageable);

    Response<List<Long>> getIdCustomerBySearchKeyWords(String searchKeywords);

    Response<Page<ExportCustomerDTO>> findAllCustomer(Pageable pageable);

}

