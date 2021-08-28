package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.List;

public interface OrderReturnService {
    CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id);
    OrderReturnDetailDTO getOrderReturnDetail(Long orderReturnId);
    SaleOrder createOrderReturn(OrderReturnRequest request, Long shopId, String userName);
    CoverResponse<List<SaleOrderDTO>,TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter,Long id);
    OrderReturnDetailDTO getSaleOrderChosen(Long id, Long shopId);
}
