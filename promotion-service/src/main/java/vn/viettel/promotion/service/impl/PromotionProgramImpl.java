package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.PromotionProductRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public PromotionProgramDTO getPromotionProgramByCode(String code) {
        PromotionProgram response = repository.findByPromotionProgramCode(code);
        if (response != null)
            return modelMapper.map(response, PromotionProgramDTO.class);
        throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
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
    public List<PromotionProgramDetailDTO> getPromotionDetailByPromotionId(Long shopId) {
        List<PromotionProgram> promotionPrograms = getAvailablePromotionProgram(shopId);
        if (promotionPrograms.isEmpty())
            return null;

        List<PromotionProgramDetailDTO> response = new ArrayList<>();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for (PromotionProgram program : promotionPrograms) {
            List<PromotionProgramDetail> programDetails = promotionDetailRepository.getPromotionDetailByPromotionId(program.getId());
            List<PromotionProgramDetailDTO> subResponse = new ArrayList<>();

            if (!programDetails.isEmpty()) {
                for (PromotionProgramDetail programDetail : programDetails) {
                    PromotionProgramDetailDTO programDetailDTO = modelMapper.map(programDetail, PromotionProgramDetailDTO.class);
                    if (program.getType() == null)
                        throw new ValidateException(ResponseMessage.TYPE_NOT_BE_NULL);
                    programDetailDTO.setType(program.getType());
                    programDetailDTO.setPromotionProgramCode(program.getPromotionProgramCode());
                    programDetailDTO.setPromotionProgramName(program.getPromotionProgramName());
                    subResponse.add(programDetailDTO);
                }
                response.addAll(subResponse);
            }
        }
        return response;
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
        LocalDateTime firstDay = DateUtils.convertFromDate(LocalDateTime.now());
        LocalDateTime lastDay = DateUtils.convertToDate(LocalDateTime.now());

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

        if (productOpenList.isEmpty()) return null;

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
    public PromotionProgramDiscountDTO getPromotionDiscount(String discountCode, Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        LocalDateTime firstDay = DateUtils.convertFromDate(LocalDateTime.now());
        LocalDateTime lastDay = DateUtils.convertToDate(LocalDateTime.now());

        PromotionProgramDiscount discount = promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopId, firstDay, lastDay).orElse(null);
        if(discount == null && shopDTO.getParentShopId()!=null)
            discount = promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopDTO.getParentShopId(), firstDay, lastDay).orElse(null);

        if(discount == null) throw new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST);

        List<PromotionProgramDTO> programs =  this.findPromotionPrograms(shopId);
        List<Long> programIds = programs.stream().map(PromotionProgramDTO::getId).collect(Collectors.toList());
        if(programIds.contains(discount.getPromotionProgramId())) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDiscountDTO discountDTO = modelMapper.map(discount, PromotionProgramDiscountDTO.class);

            PromotionProgram program = promotionProgramRepository.getById(discount.getPromotionProgramId());
            PromotionProgramDTO programDTO = modelMapper.map(program, PromotionProgramDTO.class);
            discountDTO.setProgram(programDTO);

            return discountDTO;
        }

        return null;
    }

    public List<PromotionProgram> getAvailablePromotionProgram(Long shopId) {
        if(shopId == null) return null;
        List<Long> lst = new ArrayList<>(); lst.add(shopId);
        return repository.findAvailableProgram(lst, DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));
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
    public Double getDiscountPercent(String type, String code, Double amount) {
        return promotionDetailRepository.getDiscountPercent(type, code, amount);
    }

    @Override
    public Long checkBuyingCondition(String type, Integer quantity, Double amount, List<Long> ids) {
        return promotionDetailRepository.checkBuyingCondition(type, quantity, amount, ids);
    }

    @Override
    public List<Long> getRequiredProducts(String type) {
        List<Long> result = new ArrayList<>();
        List<BigDecimal> productIds = promotionDetailRepository.getRequiredProducts(type);

        for (BigDecimal id : productIds)
            result.add(id.longValue());

        return result;
    }

    private List<PromotionProgramDTO> findPromotionPrograms(Long shopId) {
        if (shopId == null) return null;
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        List<Long> lstShopId = new ArrayList<>();
        lstShopId.add(shopId);
        if(shopDTO.getParentShopId() != null) lstShopId.add(shopDTO.getParentShopId());

        List<PromotionProgram> programs = promotionProgramRepository.findAvailableProgram(lstShopId, DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PromotionProgramDTO> dtos  = programs.stream().map(program ->modelMapper.map(program, PromotionProgramDTO.class)).collect(Collectors.toList());
        
        return dtos;
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
    public List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId) {
        List<PromotionProgramDiscount> saleProducts = promotionDiscountRepository.findPromotionDiscountByPromotion(promotionId);
        if (saleProducts == null)
            return null;

        List<PromotionProgramDiscountDTO> detailDTOS = saleProducts.stream().map(detail ->{
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDiscountDTO dto  = modelMapper.map(detail, PromotionProgramDiscountDTO.class);
            return dto;
        }).collect(Collectors.toList());

        return detailDTOS;
    }

    @Override
    public PromotionProgramDiscountDTO updatePromotionProgramDiscount(PromotionProgramDiscountDTO discount) {
        PromotionProgramDiscount discountDB = promotionDiscountRepository.findById(discount.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PromotionProgramDiscount discountUD = modelMapper.map(discount, PromotionProgramDiscount.class);
        PromotionProgramDiscount discountNew = promotionDiscountRepository.save(discountUD);

        return modelMapper.map(discountNew, PromotionProgramDiscountDTO.class);

    }
}
