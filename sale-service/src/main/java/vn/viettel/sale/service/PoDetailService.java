package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.stock.PoDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface PoDetailService extends BaseService {
    Response<Page<PoDetail>> getAllByPoConfirmId(Long id, Pageable pageable);
}
