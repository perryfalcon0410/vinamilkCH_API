package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.messaging.MemberCustomerRequest;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.entities.MemberCustomer;

public interface MemberCustomerService extends BaseService {
    MemberCustomerDTO getMemberCustomerById(Long id);
    MemberCustomer create(MemberCustomerDTO memberCustomerDTO, Long userId);
    MemberCustomerDTO getMemberCustomerByIdCustomer(long id);
    /*
     * Cập nhật lại tiền tích lủy của khách hàng sau mua hàng
     */
    Boolean updateMemberCustomer(Long customerId, MemberCustomerRequest request);
}
