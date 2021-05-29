package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;

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

    @Override
    public List<PromotionProgramDiscountDTO> listPromotionProgramDiscountByOrderNumber(String orderNumber) {
        List<PromotionProgramDiscount> promotionProgramDiscounts =
                promotionDiscountRepository.getPromotionProgramDiscountByOrderNumber(orderNumber);

        return promotionProgramDiscounts.stream().map(promotion -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(promotion, PromotionProgramDiscountDTO.class);
        }).collect(Collectors.toList());
    }

    public PromotionProgramDTO getPromotionProgramById(long Id) {
        PromotionProgram promotionProgram = promotionProgramRepository.findById(Id).orElse(null);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(promotionProgram, PromotionProgramDTO.class);
    }

    @Override
    public List<PromotionSaleProductDTO> listPromotionSaleProductsByProductId(long productId) {
        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.getPromotionSaleProductsByProductId(productId);

        return promotionSaleProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionSaleProductDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromotionCustATTRDTO> getGroupCustomerMatchProgram(Long shopId) {
        List<PromotionProgram> programList = getAvailablePromotionProgram(shopId);

        if (programList.size() == 0)
            return null;

        List<Long> ids = new ArrayList<>();
        for (PromotionProgram program : programList) {
            ids.add(program.getId());
        }
        List<PromotionCustATTR> results = promotionCusAttrRepository.getListCustomerAttributed(ids);

        return results.stream().map(promotion -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(promotion, PromotionCustATTRDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromotionProgramDetailDTO> getPromotionDetailByPromotionId(Long id) {
        List<PromotionProgramDetail> programDetails = promotionDetailRepository.getPromotionDetailByPromotionId(id);

        if (programDetails.size() == 0)
            return null;

        return programDetails.stream().map(program -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(program, PromotionProgramDetailDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromotionProgramProductDTO> getRejectProduct(List<Long> ids) {
        List<PromotionProgramProduct> programProducts = promotionProductRepository.findRejectedProject(ids);

        if (programProducts == null)
            return null;

        return programProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProgramProductDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId) {
        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if (promotionShopMap == null)
            return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(promotionShopMap, PromotionShopMapDTO.class);
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
    public List<PromotionSaleProductDTO> getZmPromotionByProductId(long productId) {

        List<PromotionSaleProduct> promotionSaleProducts =
                promotionSaleProductRepository.findByProductId(productId);
        if (promotionSaleProducts == null)
            return null;

        return promotionSaleProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionSaleProductDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromotionProductOpenDTO> getFreeItems(long programId) {
        List<PromotionProductOpen> productOpenList =
                promotionProductOpenRepository.findByPromotionProgramId(programId);

        if (productOpenList == null)
            return null;

        return productOpenList.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProductOpenDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromotionProgramDiscountDTO> getPromotionDiscounts(List<Long> ids, String cusCode) {
        List<PromotionProgramDiscount> programDiscounts = promotionDiscountRepository.findPromotionDiscount(ids, cusCode);

        return programDiscounts.stream().map(program -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(program, PromotionProgramDiscountDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public PromotionProgramDiscountDTO getPromotionDiscount(String cusCode) {
        PromotionProgramDiscount discount = promotionDiscountRepository.findByDiscountCodeAndStatusAndIsUsed(cusCode, 1, 0)
            .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_CODE_REJECT));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(discount, PromotionProgramDiscountDTO.class);
    }

    public List<PromotionProgram> getAvailablePromotionProgram(Long shopId) {
        List<PromotionProgram> promotionPrograms = repository.findAvailableProgram(shopId);
        if (promotionPrograms == null)
            return null;
        return promotionPrograms;
    }

    @Override
    public Boolean isReturn(String code) {
        if(code == null) return false ;
        else {
            PromotionProgram program =  promotionProgramRepository.findByCode(code);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDTO dto = modelMapper.map(program, PromotionProgramDTO.class);
            if(dto.getIsReturn() == 0) return true;
            else return false;
        }
    }
}
