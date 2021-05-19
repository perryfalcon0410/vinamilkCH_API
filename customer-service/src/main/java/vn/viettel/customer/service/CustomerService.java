package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.service.dto.*;

import java.util.List;

public interface CustomerService extends BaseService {

    CustomerDTO create(CustomerRequest customerRequest, Long userId, Long shopId);

    Response<CustomerDTO> getCustomerById(Long id);

    Response<CustomerDTO> getCustomerByMobiPhone(String phone );

    CustomerDTO update(CustomerRequest request, Long userId);

    Page<CustomerDTO> index(CustomerFilter filter, Pageable pageable);

    Response<List<Long>> getIdCustomerBySearchKeyWords(String searchKeywords);

    Response<List<ExportCustomerDTO>> findAllCustomer();

    Response<CustomerDTO> getCustomerDefault(Long shopId);

}

