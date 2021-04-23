package vn.viettel.sale.service;

import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface RedInvoiceDetailService extends BaseService {
    Response<RedInvoiceDetail> getRedInvoiceDetailByRedInvoiceId(Long id);
}
