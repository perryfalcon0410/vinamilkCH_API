package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.service.PoConfirmService;

import java.util.Optional;

@Service
public class PoConfirmServiceImpl extends BaseServiceImpl<PoConfirm, PoConfirmRepository> implements PoConfirmService {

    @Override
    public Response<PoConfirm> getPoConfirmById(Long id) {
        Optional<PoConfirm> poConfirm = repository.findById(id);
        if(!poConfirm.isPresent())
        {
            throw new ValidateException(ResponseMessage.PO_CONFIRM_NOT_EXISTS);
        }
        return new Response<PoConfirm>().withData(poConfirm.get());
    }
}