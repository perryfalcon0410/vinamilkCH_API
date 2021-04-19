package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.viettel.core.messaging.Response;

import vn.viettel.promotion.entities.PromotionCustATTR;
import vn.viettel.promotion.entities.PromotionProgram;
import vn.viettel.promotion.entities.PromotionProgramDiscount;
import vn.viettel.promotion.entities.PromotionSaleProduct;
import vn.viettel.promotion.entities.PromotionProgramDetail;
import vn.viettel.promotion.entities.PromotionProgramProduct;
import vn.viettel.promotion.entities.PromotionShopMap;
import vn.viettel.promotion.entities.PromotionProductOpen;
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
    @Autowired
    PromotionShopMapRepository promotionShopMapRepository;
    @Autowired
    PromotionProductOpenRepository promotionProductOpenRepository;
    @Autowired
    PromotionProgramDiscountRepository promotionDiscountRepository;

    public Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(String orderNumber) {
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

    @Override
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
    public Response<List<PromotionProgramProduct>> getRejectProduct(List<Long> ids) {
        Response<List<PromotionProgramProduct>> response = new Response<>();
        List<PromotionProgramProduct> programProducts = promotionProductRepository.findRejectedProject(ids);

        if (programProducts == null)
            return response.withData(null);
        return response.withData(programProducts);
    }

    @Override
    public Response<PromotionShopMap> getPromotionShopMap(Long promotionProgramId, Long shopId) {
        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if (promotionShopMap == null)
            return new Response<PromotionShopMap>().withData(null);
        return new Response<PromotionShopMap>().withData(promotionShopMap);
    }

    @Override
    public void saveChangePromotionShopMap(PromotionShopMap promotionShopMap, float amountReceived, Integer quantityReceived) {
        float currentAmount = promotionShopMap.getAmountReceived();
        currentAmount += amountReceived;
        long currentQuantity = promotionShopMap.getQuantityReceived();
        if (quantityReceived != null)
            currentQuantity += quantityReceived;

        promotionShopMap.setAmountReceived(currentAmount);
        promotionShopMap.setQuantityReceived(currentQuantity);
        promotionShopMapRepository.save(promotionShopMap);
    }

    // get zm promotion
    @Override
    public Response<List<PromotionSaleProduct>> getZmPromotionByProductId(
            long productId) {

        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.findByProductId(productId);
        if (promotionSaleProducts == null)
            return new Response<List<PromotionSaleProduct>>().withData(null);
        return new Response<List<PromotionSaleProduct>>().withData(promotionSaleProducts);
    }

    @Override
    public Response<List<PromotionProductOpen>> getFreeItems(long programId) {
        List<PromotionProductOpen> productOpenList =
                promotionProductOpenRepository.findByPromotionProgramId(programId);

        if (productOpenList == null)
            return new Response<List<PromotionProductOpen>>().withData(null);
        return new Response<List<PromotionProductOpen>>().withData(productOpenList);
    }

    @Override
    public Response<List<PromotionProgramDiscount>> getPromotionDiscount(List<Long> ids, String cusCode) {
        List<PromotionProgramDiscount> programDiscounts = promotionDiscountRepository.findPromotionDiscount(ids, cusCode);
        return new Response<List<PromotionProgramDiscount>>().withData(programDiscounts);
    }

    public List<PromotionProgram> getAvailablePromotionProgram(Long shopId) {
        List<PromotionProgram> promotionPrograms = repository.findAvailableProgram(shopId);
        if (promotionPrograms == null)
            return null;
        return promotionPrograms;
    }

}
