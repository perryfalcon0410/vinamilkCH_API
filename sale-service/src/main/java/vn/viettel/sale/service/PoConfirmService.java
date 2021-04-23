package vn.viettel.sale.service;

import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface PoConfirmService extends BaseService {
    Response<PoConfirm> getPoConfirmById(Long id);
}
