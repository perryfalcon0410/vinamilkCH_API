package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Transactional(rollbackFor = Exception.class)
    public PromotionShopMapDTO getPromotionShopMap(Long promotionProgramId, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        List<PromotionShopMap> promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if(promotionShopMap.isEmpty() && shopDTO.getParentShopId()!=null)
            promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopDTO.getParentShopId());
        if (promotionShopMap.isEmpty()) return null;
        entityManager.refresh(promotionShopMap.get(0));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(promotionShopMap.get(0), PromotionShopMapDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromotionShopMapDTO updatePromotionShopMap(PromotionShopMapDTO shopMap) {
        PromotionShopMap promotionShopMap = promotionShopMapRepository.findById(shopMap.getId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_CANNOT_BE_NULL));
        entityManager.refresh(promotionShopMap);
        if(shopMap.getQuantityAdd() > 0) {
            double quantityReceive = promotionShopMap.getQuantityReceived() != null ? promotionShopMap.getQuantityReceived() : 0;
            if (promotionShopMap.getQuantityMax() != null &&  promotionShopMap.getQuantityMax() < (quantityReceive + shopMap.getQuantityAdd()))
                return null;
            promotionShopMap.setQuantityReceived(quantityReceive + shopMap.getQuantityAdd());
            PromotionShopMap shopMapDB =  promotionShopMapRepository.save(promotionShopMap);
            return modelMapper.map(shopMapDB, PromotionShopMapDTO.class);
        }
        return modelMapper.map(shopMap, PromotionShopMapDTO.class);
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

        if(discount == null) return null;//new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST);

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

        List<PromotionProgramDTO> programs = promotionProgramRepository.findProgramWithConditions(lstShopId, orderType, customerTypeId, memberCardId, cusCloselyTypeId
                , cusCardTypeId, DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));

        return programs;
    }

    @Override
    public List<PromotionProgramDetailDTO> findPromotionDetailByProgramId(Long programId, List<Long> productIds) {
        List<PromotionProgramDetailDTO> details = promotionDetailRepository.findByPromotionProgramIdOrderByFreeQtyDesc(programId, productIds);
        return  details;
    }

    @Override
    public List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId) {
        List<PromotionProgramDiscountDTO> saleProducts = promotionDiscountRepository.findPromotionDiscountByPromotion(promotionId);
        return saleProducts;
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

    @Override
    public Boolean checkPromotionSaleProduct(Long programId, List<Long> productIds) {
        Integer saleProduct = promotionSaleProductRepository.countDetail(programId);
        if(saleProduct == null || saleProduct < 1) return true;
        if(programId == null || productIds == null || productIds.isEmpty()) return false;
        Integer saleProducts = promotionSaleProductRepository.findByPromotionProgramIdAndStatus(programId, productIds);
        if(saleProducts != null && saleProducts > 0) return true;
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> returnMGG(String orderCode, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        List<Long> lstPromotionShopMapIds = new ArrayList<Long>();
        List<PromotionProgramDiscount> discounts = promotionDiscountRepository.getPromotionProgramDiscountByOrderNumber(orderCode);
        for(PromotionProgramDiscount discount: discounts) {
            List<PromotionShopMap> shopMapDB = promotionShopMapRepository.findByPromotionProgramIdAndShopId(discount.getPromotionProgramId(), shopDTO.getId());
            if(shopMapDB.isEmpty() && shopDTO.getParentShop() !=null)
                shopMapDB = promotionShopMapRepository.findByPromotionProgramIdAndShopId(discount.getPromotionProgramId(), shopDTO.getParentShopId());
            if(!shopMapDB.isEmpty()) {
                PromotionShopMap shopMap = shopMapDB.get(0);
                entityManager.refresh(shopMap);
                Double qtyRecived = shopMap.getQuantityReceived()!=null?shopMap.getQuantityReceived():0;
                if(qtyRecived - discount.getActualDiscountAmount() >= 0) {
                    shopMap.setQuantityReceived(qtyRecived - discount.getActualDiscountAmount());
                    promotionShopMapRepository.save(shopMap);
                    lstPromotionShopMapIds.add(shopMap.getId());
                }
            }
            discount.setIsUsed(0);
            discount.setOrderDate(null);
            discount.setOrderCustomerCode(null);
            discount.setActualDiscountAmount(null);
            discount.setOrderShopCode(null);
            discount.setOrderAmount(null);
            discount.setOrderNumber(null);
            promotionDiscountRepository.save(discount);

        }

        return lstPromotionShopMapIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> returnPromotionShopmap(Map<String, Double> shopMaps, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();

        List<Long> lstPromotionShopMapIds = new ArrayList<Long>();
        for (Map.Entry<String, Double> entry : shopMaps.entrySet()) {
            PromotionShopMap shopMapDB = promotionShopMapRepository.findByPromotionProgramCode(entry.getKey(), shopDTO.getId());
            if(shopMapDB == null && shopDTO.getParentShop() !=null)
                shopMapDB = promotionShopMapRepository.findByPromotionProgramCode(entry.getKey(), shopDTO.getParentShop().getId());
            if(shopMapDB!=null) {
                entityManager.refresh(shopMapDB);
                Double qtyRecived = shopMapDB.getQuantityReceived()!=null?shopMapDB.getQuantityReceived():0;
                if(qtyRecived - entry.getValue() >= 0) {
                    shopMapDB.setQuantityReceived(qtyRecived - entry.getValue().longValue());
                    promotionShopMapRepository.save(shopMapDB);
                    lstPromotionShopMapIds.add(shopMapDB.getId());
                }
            }
        }

        return lstPromotionShopMapIds;
    }

    @Override
    public PromotionProgramDTO getPromotionProgram(String code) {
        if(code == null || code.isEmpty()) return null;
        PromotionProgram program = repository.findByCodeAndStatus(code, 1);
        if(program!=null) return modelMapper.map(program, PromotionProgramDTO.class);
        return null;
    }
}
