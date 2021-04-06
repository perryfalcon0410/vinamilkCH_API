package vn.viettel.promotion.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.voucher.RptCusMemAmount;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.RptCusMemAmountRepository;
import vn.viettel.promotion.service.RptCusMemAmountService;

@Service
public class RptCusMemAmountServiceImpl extends BaseServiceImpl<RptCusMemAmount, RptCusMemAmountRepository> implements RptCusMemAmountService {
    @Override
    public Response<RptCusMemAmount> findByCustomerId(Long id) {
        return new Response<RptCusMemAmount>().withData(repository.findByCustomerIdAndDeletedAtIsNull(id).get());
    }
}
