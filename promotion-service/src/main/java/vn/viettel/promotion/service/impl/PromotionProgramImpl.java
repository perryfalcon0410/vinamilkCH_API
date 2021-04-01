package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.promotion.PromotionSaleProduct;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.repository.PromotionProgramRepository;
import vn.viettel.promotion.repository.PromotionRepository;
import vn.viettel.promotion.repository.PromotionSaleProductRepository;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionProgramImpl extends BaseServiceImpl<PromotionProgram, PromotionProgramRepository>
        implements PromotionProgramService {

    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionProgramRepository promotionProgramRepository;
    @Autowired
    PromotionSaleProductRepository promotionSaleProductRepository;
    @Autowired
    PromotionCusAttrRepository promotionCusAttrRepository;
    @Autowired
    PromotionProgramDetailRepository promotionDetailRepository;
    @Autowired
    PromotionProgramProductRepository promotionProductRepository;

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

    @Override
    public Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(Long shopId) {
        Response<List<PromotionCustATTR>> response = new Response<>();
        List<PromotionProgram> programList = getAvailablePromotionProgram(shopId);

        if (programList.size() == 0)
            return response.withData(null);

        List<Long> ids = new ArrayList<>();
        for (PromotionProgram program : programList) {
            ids.add(program.getId());
        }
        List<PromotionCustATTR> result = promotionCusAttrRepository.getListCustomerAttributed(ids);
        return response.withData(result);
    }

    @Override
    public Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(Long id) {
        Response<List<PromotionProgramDetail>> response = new Response<>();
        List<PromotionProgramDetail> programDetails = promotionDetailRepository.getPromotionDetailByPromotionId(id);

        if (programDetails.size() == 0)
            return response.withData(null);
        return response.withData(programDetails);
    }

    @Override
    public Response<List<PromotionProgramProduct>> getRejectProduct(List<Long> ids, Long productId) {
        Response<List<PromotionProgramProduct>> response = new Response<>();
        List<PromotionProgramProduct> programProducts = promotionProductRepository.findRejectedProject(ids, productId);

        if (programProducts == null)
            return response.withData(null);
        return response.withData(programProducts);
    }

    public List<PromotionProgram> getAvailablePromotionProgram(Long shopId) {
        List<PromotionProgram> promotionPrograms = repository.findAvailableProgram(shopId);
        if (promotionPrograms == null)
            return null;
        return promotionPrograms;
    }
}
