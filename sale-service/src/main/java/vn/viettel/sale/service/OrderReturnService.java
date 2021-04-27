package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.OrderReturnTotalResponse;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.messaging.OrderReturnRequest;

public interface OrderReturnService {
    Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>> getAllOrderReturn(Pageable pageable);
    Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId);
    Response<SaleOrder> createOrderReturn(OrderReturnRequest request);
}
