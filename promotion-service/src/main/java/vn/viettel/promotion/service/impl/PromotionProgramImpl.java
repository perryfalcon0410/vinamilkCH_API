package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.messaging.Response;

import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.PromotionProgramRepository;
import vn.viettel.promotion.repository.PromotionSaleProductRepository;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionProgramImpl extends BaseServiceImpl<PromotionProgram, PromotionProgramRepository>
        implements PromotionProgramService {

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

    public Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(String orderNumber) {
        List<PromotionProgramDiscount> promotionProgramDiscounts =
                promotionDiscountRepository.getPromotionProgramDiscountByOrderNumber(orderNumber);

        List<PromotionProgramDiscountDTO> dtos = promotionProgramDiscounts.stream().map(promotion -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(promotion, PromotionProgramDiscountDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionProgramDiscountDTO>>().withData(dtos);
    }

    public Response<PromotionProgramDTO> getPromotionProgramById(long Id) {
        PromotionProgram promotionProgram = promotionProgramRepository.findById(Id).orElse(null);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PromotionProgramDTO dto = modelMapper.map(promotionProgram, PromotionProgramDTO.class);
        return new Response<PromotionProgramDTO>().withData(dto);
    }

    @Override
    public Response<List<PromotionSaleProductDTO>> listPromotionSaleProductsByProductId(long productId) {
        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.getPromotionSaleProductsByProductId(productId);
       List<PromotionSaleProductDTO> dtos = promotionSaleProducts.stream().map(product -> {
           modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
           return modelMapper.map(product, PromotionSaleProductDTO.class);
       }).collect(Collectors.toList());

        return new Response<List<PromotionSaleProductDTO>>().withData(dtos);
    }

    @Override
    public Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(Long shopId) {
        List<PromotionProgram> programList = getAvailablePromotionProgram(shopId);

        if (programList.size() == 0)
            return new Response<List<PromotionCustATTRDTO>>().withData(null);

        List<Long> ids = new ArrayList<>();
        for (PromotionProgram program : programList) {
            ids.add(program.getId());
        }
        List<PromotionCustATTR> results = promotionCusAttrRepository.getListCustomerAttributed(ids);
        List<PromotionCustATTRDTO> dtos = results.stream().map(promotion -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(promotion, PromotionCustATTRDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionCustATTRDTO>>().withData(dtos);
    }

    @Override
    public Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(Long id) {
        List<PromotionProgramDetail> programDetails = promotionDetailRepository.getPromotionDetailByPromotionId(id);

        if (programDetails.size() == 0)
            return new Response<List<PromotionProgramDetailDTO>>().withData(null);

        List<PromotionProgramDetailDTO> dtos = programDetails.stream().map(program -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(program, PromotionProgramDetailDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionProgramDetailDTO>>().withData(dtos);
    }

    @Override
    public Response<List<PromotionProgramProductDTO>> getRejectProduct(List<Long> ids) {
        List<PromotionProgramProduct> programProducts = promotionProductRepository.findRejectedProject(ids);

        if (programProducts == null)
            return new Response<List<PromotionProgramProductDTO>>().withData(null);

        List<PromotionProgramProductDTO> dtos = programProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProgramProductDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionProgramProductDTO>>().withData(dtos);
    }

    @Override
    public Response<PromotionShopMapDTO> getPromotionShopMap(Long promotionProgramId, Long shopId) {
        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if (promotionShopMap == null)
            return new Response<PromotionShopMapDTO>().withData(null);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new Response<PromotionShopMapDTO>().withData(modelMapper.map(promotionShopMap, PromotionShopMapDTO.class));
    }

    @Override
    public void saveChangePromotionShopMap(PromotionShopMapDTO promotionShopMap, float amountReceived, Integer quantityReceived) {
        float currentAmount = promotionShopMap.getAmountReceived();
        currentAmount += amountReceived;
        long currentQuantity = promotionShopMap.getQuantityReceived();
        if (quantityReceived != null)
            currentQuantity += quantityReceived;

        promotionShopMap.setAmountReceived(currentAmount);
        promotionShopMap.setQuantityReceived(currentQuantity);
        promotionShopMapRepository.save(modelMapper.map(promotionShopMap, PromotionShopMap.class));
    }

    // get zm promotion
    @Override
    public Response<List<PromotionSaleProductDTO>> getZmPromotionByProductId(long productId) {

        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.findByProductId(productId);
        if (promotionSaleProducts == null)
            return new Response<List<PromotionSaleProductDTO>>().withData(null);

        List<PromotionSaleProductDTO> dtos = promotionSaleProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionSaleProductDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionSaleProductDTO>>().withData(dtos);
    }

    @Override
    public Response<List<PromotionProductOpenDTO>> getFreeItems(long programId) {
        List<PromotionProductOpen> productOpenList =
                promotionProductOpenRepository.findByPromotionProgramId(programId);

        if (productOpenList == null)
            return new Response<List<PromotionProductOpenDTO>>().withData(null);

        List<PromotionProductOpenDTO> dtos = productOpenList.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProductOpenDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionProductOpenDTO>>().withData(dtos);
    }

    @Override
    public Response<List<PromotionProgramDiscountDTO>> getPromotionDiscount(List<Long> ids, String cusCode) {
        List<PromotionProgramDiscount> programDiscounts = promotionDiscountRepository.findPromotionDiscount(ids, cusCode);
        List<PromotionProgramDiscountDTO> dtos = programDiscounts.stream().map(program -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(program, PromotionProgramDiscountDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<PromotionProgramDiscountDTO>>().withData(dtos);
    }

    public List<PromotionProgram> getAvailablePromotionProgram(Long shopId) {
        List<PromotionProgram> promotionPrograms = repository.findAvailableProgram(shopId);
        if (promotionPrograms == null)
            return null;
        return promotionPrograms;
    }

}
