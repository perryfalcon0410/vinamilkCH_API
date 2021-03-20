package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerCreateRequest;
import vn.viettel.customer.messaging.CustomerDeleteRequest;
import vn.viettel.customer.messaging.CustomerUpdateRequest;
import vn.viettel.customer.service.dto.*;

import java.util.Date;
import java.util.List;

public interface CustomerService extends BaseService {
    Response<Page<CustomerResponse>> index(String searchKeywords, Date fromDate, Date toDate, Long groupId, Long status, Long gender, String areaAddress, Pageable pageable);

    Response<Customer> create(CustomerCreateRequest customerCreateRequest, Long userId);

    Response<CustomerDTO> edit(Long id);

    Response<CustomerDTO> update(CustomerUpdateRequest request, Long id, Long userId);

    Response<CustomerDTO> delete(CustomerDeleteRequest request);

    Response<List<Response<CustomerDTO>>> deleteBulk(CustomerBulkDeleteRequest request);
}

