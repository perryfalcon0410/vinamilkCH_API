package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
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
import vn.viettel.sale.specification.ComboProductSpecification;

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

    @Override
    public Response<List<ComboProductDTO>> findComboProducts(String keyWord, Integer status) {
        List<ComboProduct> comboProducts = repository.findAll(
            Specification.where(ComboProductSpecification.hasKeyWord(keyWord)).and(ComboProductSpecification.hasStatus(status)));
        List<ComboProductDTO> dtos = comboProducts.stream().map(this::convertToComboProductDTO).collect(Collectors.toList());

        return new Response<List<ComboProductDTO>>().withData(dtos);
    }

    @Override
    public Response<ComboProductDTO> getComboProduct(Long id) {
        ComboProduct comboProduct = repository.findById(id)
            .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));

        ComboProductDTO dto = this.convertToComboProductDTO(comboProduct);
        List<ComboProductDetail> details = comboProductDetailRepo.findByComboProductIdAndStatus(id, 1);
        List<ComboProductDetailDTO> detailDTOS = details.stream().map( detail -> {
            ComboProductDetailDTO detailDTO = this.convertToComboProductDetailDTO(detail);
            detailDTO.setComboProductCode(dto.getProductCode());
            return detailDTO;
        }).collect(Collectors.toList());
        dto.setDetails(detailDTOS);

        return new Response<ComboProductDTO>().withData(dto);
    }

    private ComboProductDTO convertToComboProductDTO(ComboProduct comboProduct) {
        Price price = productPriceRepo.getByASCCustomerType(comboProduct.getRefProductId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.NO_PRICE_APPLIED));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductDTO dto = modelMapper.map(comboProduct, ComboProductDTO.class);
        dto.setProductPrice(price.getPrice());
        return dto;
    }

    private ComboProductDetailDTO convertToComboProductDetailDTO(ComboProductDetail detail) {
        Price price = productPriceRepo.getByASCCustomerType(detail.getProductId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.NO_PRICE_APPLIED));
        Product product = productRepo.findById(detail.getProductId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductDetailDTO dto = modelMapper.map(detail, ComboProductDetailDTO.class);
        dto.setProductPrice(price.getPrice());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        return dto;
    }

}
