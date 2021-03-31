package vn.viettel.promotion.service;

import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.service.dto.MemberCustomerDTO;

import java.util.Optional;

public interface MemberCustomerService extends BaseService {
    Optional<MemberCustomer> getMemberCustomerById(Long id);
    Response<MemberCustomer> create(MemberCustomerDTO memberCustomerDTO, Long userId);
}
