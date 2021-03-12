package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.db.entity.StockTotal;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.SaleOrderRequest;

public interface SaleService {
    Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId);
}
