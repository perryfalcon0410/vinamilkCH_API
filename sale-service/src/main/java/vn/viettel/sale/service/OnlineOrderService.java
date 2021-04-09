package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

public interface OnlineOrderService extends BaseService {

    Response<Page<OnlineOrderDTO>> getOnlineOrders(
            OnlineOrderFilter filter, Pageable pageable);

    Response<OnlineOrderDTO> getOnlineOrder(Long id, Long shopid);
}
