package vn.viettel.sale.service;

import vn.viettel.core.db.entity.sale.RedInvoiceDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface RedInvoiceDetailService extends BaseService {
    Response<RedInvoiceDetail> getRedInvoiceDetailByRedInvoiceId(Long id);
}
