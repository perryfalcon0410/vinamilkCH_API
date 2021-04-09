package vn.viettel.sale.service;

import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface PoConfirmService extends BaseService {
    Response<PoConfirm> getPoConfirmById(Long id);
}
