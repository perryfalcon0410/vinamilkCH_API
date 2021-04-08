package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.messaging.ProductInfoFilter;
import vn.viettel.sale.repository.ProductInfoRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.specification.ProductInfoSpecification;
import vn.viettel.sale.specification.ProductSpecification;

import java.math.BigDecimal;


@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductRepository> implements ProductService {

    @Autowired
    ProductInfoRepository productInfoRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepo;

    @Override
    public Response<Page<ProductInfo>> findAllProductInfo(ProductInfoFilter filter, Pageable pageable) {
        Page<ProductInfo> productInfos
            = productInfoRepo.findAll(Specification.where(
                    ProductInfoSpecification.hasStatus(filter.getStatus()).and(ProductInfoSpecification.hasType(filter.getType()))), pageable);

        return new Response<Page<ProductInfo>>().withData(productInfos);
    }

    @Override
    public Response<Page<ProductDTO>> findProductByProductInfo(
        ProductInfoFilter filter, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
            ProductSpecification.hasProductInfo(filter.getProductInfoId()).and(ProductSpecification.hasStatus(filter.getStatus()))), pageable);
        Page<ProductDTO> productDTOS  = products.map(product -> this.mapProductToProductDTO(product, filter.getCustomerTypeId()));

        return new Response<Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<ProductDTO> getProduct(Long id, Long customerTypeId) {
        Product product = repository.findById(id).orElse(null);
        if(product == null)
            throw  new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        ProductDTO productDTO = this.mapProductToProductDTO(product, customerTypeId);
        return new Response<ProductDTO>().withData(productDTO);
    }

    @Override
    public Response<Page<ProductDTO>> findProductsByNameOrCode(ProductFilter filter, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
            ProductSpecification.hasCodeOrName(filter.getKeyWord()).and(ProductSpecification.hasStatus(filter.getStatus()))), pageable);
        Page<ProductDTO> productDTOS = products.map(product -> this.mapProductToProductDTO(product, filter.getCustomerTypeId()));

        return new Response< Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<Page<ProductDTO>> findProductsTopSale(ProductFilter filter, Pageable pageable) {
        Page<BigDecimal> shopIds = saleOrderDetailRepo.findProductTopSale(filter.getShopId(), pageable);
        Page<ProductDTO> productDTOS = shopIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), filter.getCustomerTypeId()));
        return new Response<Page<ProductDTO>>().withData(productDTOS);
    }

    private ProductDTO mapProductIdToProductDTO(Long productId, Long customerTypeId) {
        Product product = repository.findById(productId).orElse(null);
        if(product != null ) return this.mapProductToProductDTO(product, customerTypeId);
        return null;
    }

    private ProductDTO mapProductToProductDTO(Product product, Long customerTypeId) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if(productPrice != null) dto.setPrice(productPrice.getPrice());

        return dto;
    }

}
