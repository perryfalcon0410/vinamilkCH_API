package vn.viettel.customer.service;

import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface RptCusMemAmountService extends BaseService {
    RptCusMemAmountDTO findByCustomerId(Long id);
}
