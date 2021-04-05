package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import java.util.Date;

public interface OnlineOrderService extends BaseService {

    Response<Page<OnlineOrderDTO>> getOnlineOrders(
        String orderNumber, Long shopId, Integer synStatus, Date fromDate, Date toDate, Pageable pageable);
}
