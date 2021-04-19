package vn.viettel.report.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.report.repository.ProductInfoRepository;
import vn.viettel.report.repository.ProductRepository;
import vn.viettel.report.service.ProductService;
import vn.viettel.report.service.dto.ProductDTO;
import vn.viettel.report.service.dto.ProductInfoDTO;
import vn.viettel.report.specification.ProductsSpecification;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductRepository> implements ProductService {
    @Autowired
    ProductInfoRepository productInfoRepository;


    private ProductDTO mapProductToProductDTO(Product product) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        return dto;
    }
    private ProductInfoDTO mapToProductInfoDTO(ProductInfo productInfo) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductInfoDTO dto = modelMapper.map(productInfo, ProductInfoDTO.class);
        return dto;
    }
    @Override
    public Response<Page<ProductDTO>> findProduct(List<String> productCodes,String productName,Long catId, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
                ProductsSpecification.hasProductCode(productCodes)).and(ProductsSpecification.hasProductName(productName)).and(ProductsSpecification.hasCatId(catId)), pageable);
        Page<ProductDTO> productDTOS = products.map(this::mapProductToProductDTO);
        return new Response< Page<ProductDTO>>().withData(productDTOS);
    }
    @Override
    public Response<List<ProductInfoDTO>> getAllProductCat() {
        List<ProductInfo> productInfo = productInfoRepository.getAllProductInfo();
        List<ProductInfoDTO> list = productInfo.stream().map(
                item -> modelMapper.map(item, ProductInfoDTO.class)
        ).collect(Collectors.toList());
        return new Response< List<ProductInfoDTO>>().withData(list);
    }
}
