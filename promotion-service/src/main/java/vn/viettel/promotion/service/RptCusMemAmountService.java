package vn.viettel.promotion.service;

import vn.viettel.core.db.entity.voucher.RptCusMemAmount;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface RptCusMemAmountService extends BaseService {
    Response<RptCusMemAmount> findByCustomerId(Long id);
}
