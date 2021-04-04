package vn.viettel.sale.service;

import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SaleService {
    Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId);
    Response<Shop> getShopById(long id);
    Response<List<ZmFreeItemDTO>> getFreeItems(List<OrderDetailDTO> productList);
}
