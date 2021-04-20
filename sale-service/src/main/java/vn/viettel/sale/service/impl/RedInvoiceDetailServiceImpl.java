package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.RedInvoiceDetailRepository;
import vn.viettel.sale.service.RedInvoiceDetailService;

import java.util.Optional;

@Service
public class RedInvoiceDetailServiceImpl extends BaseServiceImpl<RedInvoiceDetail, RedInvoiceDetailRepository> implements RedInvoiceDetailService {
    @Override
    public Response<RedInvoiceDetail> getRedInvoiceDetailByRedInvoiceId(Long id) {
        Optional<RedInvoiceDetail> redInvoiceDetail = repository.getByRedInvoiceId(id);
        if(!redInvoiceDetail.isPresent())
            throw new ValidateException(ResponseMessage.RED_INVOICE_DETAIL_NOT_EXISTS);
        return new Response<RedInvoiceDetail>().withData(redInvoiceDetail.get());
    }
}
