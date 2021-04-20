package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.sale.RedInvoiceDetail;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.RptCusMemAmountService;

import java.util.Optional;

@Service
public class RptCusMemAmountServiceImpl extends BaseServiceImpl<RptCusMemAmount, RptCusMemAmountRepository> implements RptCusMemAmountService {
    @Override
    public Response<RptCusMemAmountDTO> findByCustomerId(Long id) {
        Optional<RptCusMemAmount> rptCusMemAmount = repository.findByCustomerIdAndStatus(id, 1);
        return new Response<RptCusMemAmountDTO>().withData(modelMapper.map(rptCusMemAmount.get(), RptCusMemAmountDTO.class));
    }
}
