package vn.viettel.customer.service;

import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.MemberCardCreateRequest;

public interface MemberCardService extends BaseService {
    Response<MemberCard> create (MemberCardCreateRequest request,Long userId);
}
