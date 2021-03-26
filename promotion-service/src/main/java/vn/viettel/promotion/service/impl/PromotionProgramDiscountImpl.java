package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.promotion.repository.PromotionRepository;
import vn.viettel.promotion.service.PromotionProgramDiscountService;

@Service
public class PromotionProgramDiscountImpl implements PromotionProgramDiscountService {
    @Autowired
    PromotionRepository promotionRepository;

//    public Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber) {
//        Response<List<PromotionProgramDiscount>> response = new Response<>();
//        List<PromotionProgramDiscount> promotionProgramDiscounts = promotionProgramDiscountRepository.getListPromotionByOrderNumber(orderNumber);
//        response.setData(promotionProgramDiscounts);
//        return response;
//    }
}
