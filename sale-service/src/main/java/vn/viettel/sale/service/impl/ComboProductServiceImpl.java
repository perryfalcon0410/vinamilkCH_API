package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.repository.ComboProductDetailRepository;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;
import vn.viettel.sale.service.dto.ComboProductDetailDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.specification.ComboProductSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboProductServiceImpl extends BaseServiceImpl<ComboProduct, ComboProductRepository> implements ComboProductService {

    @Autowired
    ComboProductDetailRepository comboProductDetailRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    CustomerClient customerClient;

    @Override
    public List<ComboProductDTO> findComboProducts(Long shopId, String keyWord, Integer status) {
        List<ComboProductDTO> dtos = new ArrayList<>();
        List<ComboProduct> comboProducts = repository.findAll(
            Specification.where(ComboProductSpecification.hasKeyWord(keyWord))
                    .and(ComboProductSpecification.hasStatus(status)));
        Long customerTypeId = null;
        List<Long> lstProductIds = comboProducts.stream().map(item -> item.getRefProductId()).distinct().collect(Collectors.toList());

        if(lstProductIds.isEmpty()) return dtos;
        LocalDateTime now = DateUtils.convertToDate(LocalDateTime.now());
        List<Price> prices = productPriceRepo.findProductPriceWithType(lstProductIds, customerTypeId, now);
        for (ComboProduct combo: comboProducts) {
            for (Price price : prices) {
                if (price.getProductId().equals(combo.getRefProductId())) {
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    ComboProductDTO dto = modelMapper.map(combo, ComboProductDTO.class);
                    dto.setProductPrice(price.getPrice());
                    dtos.add(dto);
                    break;
                }
            }
        }

        return dtos;
    }

    @Override
    public ComboProductDTO getComboProduct(Long shopId, Long comboProductId) {
        ComboProduct comboProduct = repository.findById(comboProductId)
            .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));
        //Lấy giá theo Kh type = -1,  ASC lấy giá đầu tiền
        Long customerTypeId = null;
        List<Price> prices1 = productPriceRepo.findProductPriceWithType(Arrays.asList(comboProduct.getRefProductId()), customerTypeId, DateUtils.convertToDate(LocalDateTime.now()));
        ComboProductDTO dto = this.convertToComboProductDTO(comboProduct, prices1);
        List<ComboProductDetail> details = comboProductDetailRepo.findByComboProductIdAndStatus(comboProductId, 1);
        if(details != null){
            List<Long> lstProductIds = details.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
            List<Product> products = productRepo.getProducts(lstProductIds, 1);
            List<Price> prices = productPriceRepo.findProductPriceWithType(lstProductIds, customerTypeId, DateUtils.convertToDate(LocalDateTime.now()));
            List<ComboProductDetailDTO> detailDTOS = details.stream().map( detail -> {
                ComboProductDetailDTO detailDTO = this.convertToComboProductDetailDTO(detail, products, prices);
                detailDTO.setComboProductCode(dto.getProductCode());
                return detailDTO;
            }).collect(Collectors.toList());
            dto.setDetails(detailDTOS);
        }
        return dto;
    }

    private ComboProductDTO convertToComboProductDTO(ComboProduct comboProduct, List<Price> prices) {
        Price price = null;
        if(prices != null) {
            for (Price price1 : prices) {
                if (price1.getProductId().equals(comboProduct.getRefProductId())) {
                    price = price1;
                    break;
                }
            }
        }
        if(price == null) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductDTO dto = modelMapper.map(comboProduct, ComboProductDTO.class);
        dto.setProductPrice(price.getPrice());
        return dto;
    }

    private ComboProductDetailDTO convertToComboProductDetailDTO(ComboProductDetail detail, List<Product> products, List<Price> prices) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductDetailDTO dto = modelMapper.map(detail, ComboProductDetailDTO.class);
        if(prices == null || !prices.stream().map(item -> item.getProductId()).collect(Collectors.toList()).contains(detail.getProductId())){
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
        }
        if(products == null || !products.stream().map(item -> item.getId()).collect(Collectors.toList()).contains(detail.getProductId())){
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        }

        for(Price price : prices){
            if(price.getProductId().equals(detail.getProductId())){
                dto.setProductPrice(price.getPrice());
                break;
            }
        }
        for(Product product : products){
            if(product.getId().equals(detail.getProductId())){
                dto.setProductCode(product.getProductCode());
                dto.setProductName(product.getProductName());
                break;
            }
        }
        return dto;
    }
}
