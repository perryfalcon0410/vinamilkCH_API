package vn.viettel.promotion.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.voucher.RptCusMemAmount;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.RptCusMemAmountRepository;
import vn.viettel.promotion.service.RptCusMemAmountService;

import java.util.Optional;

@Service
public class RptCusMemAmountServiceImpl extends BaseServiceImpl<RptCusMemAmount, RptCusMemAmountRepository> implements RptCusMemAmountService {
    @Override
    public Response<RptCusMemAmount> findByCustomerId(Long id) {
        Optional<RptCusMemAmount> rptCusMemAmount = repository.findByCustomerIdAndStatus(id, 1);
        if(!rptCusMemAmount.isPresent())
        {
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST_IN_RPT_CUS_MEM_AMOUNT);
        }
        return new Response<RptCusMemAmount>().withData(rptCusMemAmount.get());
    }
}
