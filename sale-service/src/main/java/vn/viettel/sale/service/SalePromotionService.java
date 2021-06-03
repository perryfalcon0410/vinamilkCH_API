package vn.viettel.sale.service;

import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.service.dto.AutoPromotionDTO;

import java.util.List;

public interface SalePromotionService {
    List<AutoPromotionDTO> getFreeItems(PromotionProductRequest request, Long shopId, Long customerId);
}
