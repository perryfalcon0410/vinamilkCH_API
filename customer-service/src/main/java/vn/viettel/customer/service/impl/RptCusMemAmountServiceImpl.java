package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.service.BaseServiceImpl;
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
}
