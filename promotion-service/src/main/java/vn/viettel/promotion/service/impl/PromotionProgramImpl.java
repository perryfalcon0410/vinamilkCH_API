package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.messaging.ProductRequest;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;

import java.math.BigDecimal;
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
    public PromotionProgramDTO getPromotionProgramByCode(String code) {
        PromotionProgram response = repository.findByPromotionProgramCode(code);
        if (response != null)
            return modelMapper.map(response, PromotionProgramDTO.class);
        throw new ValidateException(ResponseMessage.PROMOTION_DOSE_NOT_EXISTS);
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
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if(promotionShopMap == null && shopDTO.getParentShopId()!=null)
            promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopDTO.getParentShopId());
        if (promotionShopMap == null) return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(promotionShopMap, PromotionShopMapDTO.class);
    }

    @Override
    public void saveChangePromotionShopMap(Long promotionProgramId, Long shopId, Double receivedQuantity) {

        PromotionShopMap promotionShopMap = promotionShopMapRepository.findByPromotionProgramIdAndShopId(promotionProgramId, shopId);
        if (promotionShopMap == null)
            throw new ValidateException(ResponseMessage.PROMOTION_SHOP_MAP_CANNOT_BE_NULL);

        if (promotionShopMap.getQuantityMax() != null) {
            promotionShopMap.setQuantityReceived(promotionShopMap.getQuantityReceived() - receivedQuantity.longValue());
        }
        promotionShopMapRepository.save(promotionShopMap);
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
    public PromotionProgramDiscountDTO getPromotionDiscount(String cusCode, Long customerId, List<ProductRequest> products) {
        PromotionProgramDiscount discount = promotionDiscountRepository.findByDiscountCodeAndStatusAndIsUsed(cusCode, 1, 0)
            .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_PROGRAM_DISCOUNT_NOT_EXIST));
        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        if(discount.getCustomerCode()!=null && !discount.getCustomerCode().equals(customer.getCustomerCode()))
            throw new ValidateException(ResponseMessage.CUSTOMER_REJECT);
        PromotionProgram program = promotionProgramRepository.findByIdAndStatus(discount.getPromotionProgramId(), 1)
            .orElseThrow(() -> new ValidateException(ResponseMessage.PROMOTION_PROGRAM_NOT_EXISTS));
        List<PromotionSaleProduct> saleProducts = promotionSaleProductRepository.findByPromotionProgramIdAndStatus(program.getId(), 1);

        if(!saleProducts.isEmpty()) {
             Map<Long, Integer> map1 = saleProducts.stream().collect(Collectors.toMap(PromotionSaleProduct::getProductId, PromotionSaleProduct::getQuantity));
            boolean exits = false;
            for (ProductRequest productRequest : products) {
                if(map1.containsKey(productRequest.getProductId()) && map1.get(productRequest.getProductId()) <= productRequest.getQuantity()) {
                    exits = true;
                    break;
                }
            }
            if (!exits) throw new ValidateException(ResponseMessage.PROMOTION_SALE_PRODUCT_REJECT);
        }

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
        if(code == null) return false;
        else {
            PromotionProgram program =  promotionProgramRepository.findByCode(code);
            if(program == null ) return false;
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDTO dto = modelMapper.map(program, PromotionProgramDTO.class);
            if(dto.getIsReturn() == 0) return true;
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

    @Override
    public List<PromotionProgramDTO> findPromotionPrograms(Long shopId) {
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        List<PromotionProgram> programs = promotionProgramRepository.findByShop(shopId);
        if(shopDTO.getParentShopId()!=null) {
            List<PromotionProgram> programsParentShop = promotionProgramRepository.findByShop(shopDTO.getParentShopId());
            programs.addAll(programsParentShop);
        }
        List<PromotionProgramDTO> dtos  = programs.stream().map(program -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDTO dto = modelMapper.map(program, PromotionProgramDTO.class);
            return dto;
        }).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public List<PromotionProgramDetailDTO> findPromotionDetailByProgramId(Long programId) {
        List<PromotionProgramDetail> details = promotionDetailRepository.findByPromotionProgramId(programId);
        List<PromotionProgramDetailDTO> detailDTOS = details.stream().map(detail ->{
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PromotionProgramDetailDTO dto  = modelMapper.map(detail, PromotionProgramDetailDTO.class);
            return dto;
        }).collect(Collectors.toList());
        return detailDTOS;
    }
}
