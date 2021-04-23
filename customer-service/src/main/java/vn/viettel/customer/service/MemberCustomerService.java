package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.entities.MemberCustomer;

public interface MemberCustomerService extends BaseService {
    Response<MemberCustomerDTO> getMemberCustomerById(Long id);
    Response<MemberCustomer> create(MemberCustomerDTO memberCustomerDTO, Long userId);
    Response<MemberCustomerDTO> getMemberCustomerByIdCustomer(long id);
}
