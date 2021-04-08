package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;

public interface OrderReturnService {
    Response<Page<OrderReturnDTO>> getAllOrderReturn(Pageable pageable);
    Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId);
}