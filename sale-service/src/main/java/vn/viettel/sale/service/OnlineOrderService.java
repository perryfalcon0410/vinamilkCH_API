package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import java.io.InputStream;
import java.util.List;

public interface OnlineOrderService extends BaseService {

    Page<OnlineOrderDTO> getOnlineOrders(
            OnlineOrderFilter filter, Pageable pageable);

    OnlineOrderDTO getOnlineOrder(Long id, Long shopId, Long userId);

    String checkOnlineNumber(String code);

    void syncXmlOnlineOrder(InputStream inputStream) throws Exception;

    void syncXmlToCancelOnlineOrder(InputStream inputStream) throws Exception;

    InputStream exportXmlFile(List<OnlineOrder> onlineOrders) throws Exception;

    void getOnlineOrderSchedule();

    void uploadOnlineOrderSchedule();

}
