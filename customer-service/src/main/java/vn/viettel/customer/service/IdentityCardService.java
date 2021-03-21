package vn.viettel.customer.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.IdentityCardCreateRequest;
import vn.viettel.customer.messaging.IdentityCardUpdateRequest;
import vn.viettel.customer.service.dto.IdentityCardDTO;

public interface IdentityCardService extends BaseService {

    Response<IdentityCardDTO> create(IdentityCardCreateRequest request, Long userId);
    Response<IdentityCardDTO> update(IdentityCardUpdateRequest request, Long userId);

}

