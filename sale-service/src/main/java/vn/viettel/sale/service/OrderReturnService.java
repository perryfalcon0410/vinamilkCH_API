package vn.viettel.sale.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalOrderChoose;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

public interface OrderReturnService {
    CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id);
    OrderReturnDetailDTO getOrderReturnDetail(Long orderReturnId);
    HashMap<String,Object> createOrderReturn(OrderReturnRequest request, Long shopId, String userName);
    CoverResponse<List<SaleOrderDTO>,TotalOrderChoose> getSaleOrderForReturn(SaleOrderChosenFilter filter,Long id);
    OrderReturnDetailDTO getSaleOrderChosen(Long id, Long shopId);
}
