package vn.viettel.sale.service;

import vn.viettel.sale.messaging.OrderPromotionRequest;
import vn.viettel.sale.messaging.SalePromotionCalculationRequest;
import vn.viettel.sale.service.dto.SalePromotionCalculationDTO;
import vn.viettel.sale.service.dto.SalePromotionDTO;

import java.util.HashMap;

public interface SalePromotionService {
    /*
    lấy danh sách các khuyến mãi cho đơn hàng
     */
    SalePromotionCalculationDTO getSaleItemPromotions(OrderPromotionRequest request, Long shopId, HashMap<Long,Double> mapMoneys, boolean forSaving);

    /*
    Tính toán khuyến mãi
     */
    SalePromotionCalculationDTO promotionCalculation(SalePromotionCalculationRequest calculationRequest, Long shopId);

    /*
    kiểm tra số số suất của 1 km, km có được sử dụng
     */
    boolean checkPromotionLimit(SalePromotionDTO salePromotion, Long shopId);

    /*
    Lấy mã giảm giá
     */
    SalePromotionDTO getDiscountCode(String discountCode, Long shopId, OrderPromotionRequest request);
}