package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionProgramDiscountImpl extends BaseServiceImpl<PromotionProgram, PromotionProgramRepository>
        implements PromotionProgramService {

    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionProgramRepository promotionProgramRepository;
    @Autowired
    PromotionCusAttrRepository promotionCusAttrRepository;
    @Autowired
    PromotionProgramDetailRepository promotionDetailRepository;
    @Autowired
    PromotionProgramProductRepository promotionProductRepository;

    public Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber) {
        Response<List<PromotionProgramDiscount>> response = new Response<>();
        List<PromotionProgramDiscount> promotionProgramDiscounts = promotionRepository.getPromotionProgramDiscountByOrderNumber(orderNumber);
        response.setData(promotionProgramDiscounts);
        return response;
    }

    public Response<PromotionProgram> getPromotionProgramById(Long Id) {
        PromotionProgram promotionProgram = promotionProgramRepository.findById(Id).get();
        Response<PromotionProgram> response = new Response<>();
        response.setData(promotionProgram);
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
