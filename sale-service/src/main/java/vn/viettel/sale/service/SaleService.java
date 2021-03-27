package vn.viettel.sale.service;

import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.SaleOrderRequest;

public interface SaleService {
    Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId);
    Response<Shop> getShopById(long id);
}
