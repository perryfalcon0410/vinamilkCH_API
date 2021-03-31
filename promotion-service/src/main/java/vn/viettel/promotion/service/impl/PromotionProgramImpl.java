package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.promotion.PromotionSaleProduct;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.repository.PromotionProgramRepository;
import vn.viettel.promotion.repository.PromotionRepository;
import vn.viettel.promotion.repository.PromotionSaleProductRepository;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.List;

@Service
public class PromotionProgramImpl implements PromotionProgramService {
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionProgramRepository promotionProgramRepository;
    @Autowired
    PromotionSaleProductRepository promotionSaleProductRepository;

    public Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber)
    {
        Response<List<PromotionProgramDiscount>> response = new Response<>();
        List<PromotionProgramDiscount> promotionProgramDiscounts =
                promotionRepository.getPromotionProgramDiscountByOrderNumber(orderNumber);
        response.setData(promotionProgramDiscounts);
        return response;
    }

    public Response<PromotionProgram> getPromotionProgramById(long Id) {
        PromotionProgram promotionProgram = promotionProgramRepository.findById(Id).get();
        Response<PromotionProgram> response = new Response<>();
        response.setData(promotionProgram);
        return response;
    }

    public Response<List<PromotionSaleProduct>> listPromotionSaleProductsByProductId(long productId) {
        Response<List<PromotionSaleProduct>> response = new Response<>();
        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.getPromotionSaleProductsByProductId(productId);
        response.setData(promotionSaleProducts);
        return response;
    }
}
