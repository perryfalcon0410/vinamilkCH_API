package vn.viettel.sale.service;

import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SalePromotionService {
    List<ZmFreeItemDTO> getFreeItems(PromotionProductRequest request, Long shopId, Long customerId);
}
