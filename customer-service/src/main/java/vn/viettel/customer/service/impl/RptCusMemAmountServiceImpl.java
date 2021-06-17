package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.RptCusMemAmountRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.RptCusMemAmountService;

@Service
public class RptCusMemAmountServiceImpl extends BaseServiceImpl<RptCusMemAmount, RptCusMemAmountRepository> implements RptCusMemAmountService {
    @Override
    public RptCusMemAmountDTO findByCustomerId(Long id) {
        RptCusMemAmount rptCusMemAmount = repository.findByCustomerIdAndStatus(id, 1).orElse(null);
        if(rptCusMemAmount == null) return null;
        return modelMapper.map(rptCusMemAmount, RptCusMemAmountDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRptCus(Long id, RptCusMemAmountRequest request) {
        RptCusMemAmount rptCusMemAmount = repository.findById(id)
            .orElseThrow(() -> new ValidateException(ResponseMessage.RPT_CUST_MEM_AMOUNT_NOT_EXEITS));
        rptCusMemAmount.setAmount(request.getAmount());
        repository.save(rptCusMemAmount);
        return true;
    }

    @Override
    public RptCusMemAmountDTO getRptCus(Long customerId, Long shopId) {
        RptCusMemAmount rptCusMemAmount = repository.findByCustomerIdAndCustShopIdAndStatus(customerId, shopId, 1)
                .orElseThrow(() -> new ValidateException(ResponseMessage.RPT_CUST_MEM_AMOUNT_NOT_EXEITS));
        return modelMapper.map(rptCusMemAmount, RptCusMemAmountDTO.class);
    }
}
