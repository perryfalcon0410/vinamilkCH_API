package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;

import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    CustomerClient customerClient;
    @Autowired
    PromotionCustATTRDetailRepository promotionCustATTRDetailRepository;

    @Autowired
    ShopClient shopClient;

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
    public List<PromotionProgramProductDTO> findByPromotionIds(List<Long> promotionIds) {
        List<PromotionProgramProduct> programProducts = promotionProductRepository.findByPromotionIds(promotionIds);

        if (programProducts == null)
            return null;

        return programProducts.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProgramProductDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();

        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if(promotionShopMap == null && shopDTO.getParentShopId()!=null)
            promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopDTO.getParentShopId());
        if (promotionShopMap == null) return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(promotionShopMap, PromotionShopMapDTO.class);
    }

    @Override
    public PromotionShopMapDTO updatePromotionShopMap(PromotionShopMapDTO shopMap) {
        promotionShopMapRepository.findById(shopMap.getId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_CANNOT_BE_NULL));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PromotionShopMap shopMapNew = modelMapper.map(shopMap, PromotionShopMap.class);
        PromotionShopMap shopMapDB =  promotionShopMapRepository.save(shopMapNew);
        return modelMapper.map(shopMapDB, PromotionShopMapDTO.class);
    }

    @Override
    public List<PromotionProductOpenDTO> getFreeItems(long programId) {
        List<PromotionProductOpen> productOpenList =
                promotionProductOpenRepository.findByPromotionProgramId(programId);

        if (productOpenList.isEmpty()) return null;

        return productOpenList.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, PromotionProductOpenDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public PromotionProgramDiscountDTO getPromotionDiscount(String discountCode, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        LocalDateTime firstDay = DateUtils.convertFromDate(LocalDateTime.now());
        LocalDateTime lastDay = DateUtils.convertToDate(LocalDateTime.now());

        PromotionProgramDiscount discount = promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopId, firstDay, lastDay).orElse(null);
        if(discount == null && shopDTO.getParentShopId()!=null)
            discount = promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopDTO.getParentShopId(), firstDay, lastDay).orElse(null);

        if(discount == null) throw null;//new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PromotionProgramDiscountDTO discountDTO = modelMapper.map(discount, PromotionProgramDiscountDTO.class);

        PromotionProgram program = promotionProgramRepository.getById(discount.getPromotionProgramId());
        PromotionProgramDTO programDTO = modelMapper.map(program, PromotionProgramDTO.class);
        discountDTO.setProgram(programDTO);

        return discountDTO;
    }

    @Override
    public Boolean isReturn(String code) {
        if(code == null) return false;
        else {
            PromotionProgram program =  promotionProgramRepository.findByCode(code);
            if(program == null ) return false;
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDTO dto = modelMapper.map(program, PromotionProgramDTO.class);
            if(dto.getIsReturn() != 1) return true;
            else return false;
        }
    }

    @Override
    public List<PromotionProgramDTO> findPromotionPrograms(Long shopId, Long orderType, Long customerTypeId, Long memberCardId, Long cusCloselyTypeId
            ,Long cusCardTypeId) {
        if (shopId == null) return null;
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        List<Long> lstShopId = new ArrayList<>();
        lstShopId.add(shopId);
        if(shopDTO.getParentShopId() != null) lstShopId.add(shopDTO.getParentShopId());

        List<PromotionProgram> programs = promotionProgramRepository.findProgramWithConditions(lstShopId, orderType, customerTypeId, memberCardId, cusCloselyTypeId
                , cusCardTypeId, DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PromotionProgramDTO> dtos  = programs.stream().map(program ->modelMapper.map(program, PromotionProgramDTO.class)).collect(Collectors.toList());

        return dtos;
    }

    @Override
    public List<PromotionProgramDetailDTO> findPromotionDetailByProgramId(Long programId) {
        List<PromotionProgramDetail> details = promotionDetailRepository.findByPromotionProgramIdOrderByFreeQtyDesc(programId);
        List<PromotionProgramDetailDTO> detailDTOS = details.stream().map(detail ->{
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDetailDTO dto  = modelMapper.map(detail, PromotionProgramDetailDTO.class);
            return dto;
        }).collect(Collectors.toList());
        return detailDTOS;
    }

    @Override
    public List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId) {
        List<PromotionProgramDiscount> saleProducts = promotionDiscountRepository.findPromotionDiscountByPromotion(promotionId);
        if (saleProducts == null)
            return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PromotionProgramDiscountDTO> detailDTOS = saleProducts.stream().map(detail ->modelMapper.map(detail, PromotionProgramDiscountDTO.class)).collect(Collectors.toList());

        return detailDTOS;
    }

    @Override
    public PromotionProgramDiscountDTO updatePromotionProgramDiscount(PromotionProgramDiscountDTO discount) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PromotionProgramDiscount discountUD = modelMapper.map(discount, PromotionProgramDiscount.class);
        PromotionProgramDiscount discountNew = promotionDiscountRepository.save(discountUD);

        return modelMapper.map(discountNew, PromotionProgramDiscountDTO.class);

    }

    @Override
    public List<PromotionProgramDTO> findByIds(List<Long> promotionIds) {
        List<PromotionProgram> programs = repository.findAllById(promotionIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (programs == null)
            return null;

        return programs.stream().map(product -> modelMapper.map(product, PromotionProgramDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PromotionSaleProductDTO> findPromotionSaleProductByProgramId(Long programId) {
        List<PromotionSaleProduct> saleProducts = promotionSaleProductRepository.findByPromotionProgramIdAndStatus(programId, 1);
        if (saleProducts == null)
            return null;
        List<PromotionSaleProductDTO> detailDTOS = saleProducts.stream().map(detail ->{
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionSaleProductDTO dto  = modelMapper.map(detail, PromotionSaleProductDTO.class);
            return dto;
        }).collect(Collectors.toList());
        return detailDTOS;
    }
}
