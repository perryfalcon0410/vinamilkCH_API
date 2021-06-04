package vn.viettel.sale.service;

import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.SalePromotionCalculationDTO;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import java.util.List;

public interface SalePromotionService {

    List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId);

    List<FreeProductDTO> getPromotionProduct(Long promotionId, Long shopId);

    SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId);

}
