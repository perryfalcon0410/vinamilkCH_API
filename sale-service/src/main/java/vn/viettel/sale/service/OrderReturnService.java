package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.OrderReturnTotalResponse;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.List;

public interface OrderReturnService {
    Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>> getAllOrderReturn(SaleOrderFilter saleOrderFilter, Pageable pageable);
    Response<OrderReturnDetailDTO> getOrderReturnDetail(long orderReturnId);
    Response<SaleOrder> createOrderReturn(OrderReturnRequest request);
    Response<List<SaleOrderDTO>> getSaleOrderForReturn(SaleOrderChosenFilter filter);
    Response<OrderReturnDetailDTO> getSaleOrderChosen(long id);
}
