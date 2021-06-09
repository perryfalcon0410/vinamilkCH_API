package vn.viettel.sale.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SaleService {
    /*
    create sale order return order id
     */
    Long createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId);
}
