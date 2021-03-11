package vn.viettel.customer.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.dto.CustomerResponse;
import vn.viettel.customer.service.dto.SearchCustomerDto;

import java.util.List;

public interface SearchCustomerService {
    Response<List<CustomerResponse>> searchCustomer(SearchCustomerDto searchInfo);
}
