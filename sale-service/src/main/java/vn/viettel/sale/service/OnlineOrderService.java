package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.Date;

public interface OnlineOrderService extends BaseService {

    Response<Page<OnlineOrder>> getOnlineOrders(
        String orderNumber, Long shopId, Integer sysStatus, Date fromDate, Date toDate, Pageable pageable);
}
