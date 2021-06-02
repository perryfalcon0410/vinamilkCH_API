package vn.viettel.sale.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SaleService {
    Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId);
    Response<List<ZmFreeItemDTO>> getFreeItems(List<ProductOrderRequest> productList, Long shopId, Long customerId);
}
