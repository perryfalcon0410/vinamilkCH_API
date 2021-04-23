package vn.viettel.sale.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SaleService {
    Response<SaleOrder> createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId, long formId, long ctrlId);
    Response<ShopDTO> getShopById(long id);
    Response<List<ZmFreeItemDTO>> getFreeItems(List<OrderDetailDTO> productList);
}
