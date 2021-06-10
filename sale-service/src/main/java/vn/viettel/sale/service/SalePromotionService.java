package vn.viettel.sale.service;

import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.SalePromotionCalculationDTO;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import java.util.List;

public interface SalePromotionService {
    /*
    lấy danh sách các khuyến mãi cho đơn hàng
     */
    List<SalePromotionDTO> getSaleItemPromotions(OrderPromotionRequest request, Long shopId, boolean forSaving);

    /*
    Tính toán khuyến mãi
     */
    SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId);

    /*
    Cập nhật thông tin khuyến mãi zv19 - zv21
     */
    List<SalePromotionDTO> updatePromotionZV19(List<SalePromotionDTO> allDiscountAmount, Long shopId);

    /*
    kiểm tra số số suất của 1 km, km có được sử dụng
     */
    boolean checkPromotionLimit(SalePromotionDTO salePromotion, Long shopId);
}
