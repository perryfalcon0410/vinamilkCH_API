package vn.viettel.sale.service;

import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface RedInvoiceDetailService extends BaseService {
    Response<List<RedInvoiceDetail>> getRedInvoiceDetailByRedInvoiceId(Long id);
}
