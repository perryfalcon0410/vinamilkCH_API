package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.*;

import java.util.List;
public interface SaleOrderService {
    Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable);
    Response<SaleOrderDetailDTO> getSaleOrderDetail(long saleOrderId, String orderNumber);
    Response<List<OrderDetailDTO>> getDetail(long saleOrderId);
}
