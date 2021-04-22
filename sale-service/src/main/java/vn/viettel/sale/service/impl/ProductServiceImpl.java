package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.specification.ProductInfoSpecification;
import vn.viettel.sale.specification.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductRepository> implements ProductService {

    @Autowired
    ProductInfoRepository productInfoRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepo;

    @Autowired
    StockTotalRepository stockTotalRepo;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Override
    public Response<Page<ProductInfo>> findAllProductInfo(Integer status, Integer type, Pageable pageable) {
        Page<ProductInfo> productInfos
                = productInfoRepo.findAll(Specification.where(
                ProductInfoSpecification.hasStatus(status).and(ProductInfoSpecification.hasType(type))), pageable);

        return new Response<Page<ProductInfo>>().withData(productInfos);
    }

    @Override
    public Response<ProductDTO> getProduct(Long id, Long customerTypeId, Long shopId) {
        Product product = repository.findById(id).orElse(null);
        if(product == null)
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if(customerType == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        ProductDTO productDTO = this.mapProductToProductDTO(
            product, customerTypeId, customerType.getWareHoseTypeId(), shopId);
        return new Response<ProductDTO>().withData(productDTO);
    }

    @Override
    public Response<Page<ProductDTO>> findProducts(ProductFilter filter, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(filter.getKeyWord())
                                    .and( ProductSpecification.hasProductInfo(filter.getProductInfoId()))
                                    .and(ProductSpecification.hasStatus(filter.getStatus()))
                                    .and(ProductSpecification.deletedAtIsNull())), pageable);
        Page<ProductDTO> productDTOS = products.map(
                product -> this.mapProductToProductDTO(product, filter.getCustomerTypeId()));

        return new Response< Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<Page<ProductDTO>> findProductsTopSale(Long shopId, String keyWord, Long customerId, Pageable pageable) {
        String nameLowerCase = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        Page<BigDecimal> shopIds = repository.findProductTopSale(shopId, keyWord, nameLowerCase, pageable);

        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if(customerType == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        Page<ProductDTO> productDTOS = shopIds.map(id ->
            this.mapProductIdToProductDTO(id.longValue(), customerId, customerType.getWareHoseTypeId(), shopId));

        return new Response<Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<OrderProductsDTO> changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> productsRequest) {
        OrderProductsDTO orderProductsDTO = new OrderProductsDTO();

        CustomerType customerType = customerTypeClient.getCusTypeIdByShopId(shopId);
        if(customerType == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductDTO> productDTOS = productsRequest.stream().map(product ->
            this.mapProductIdToProductDTO(product, customerType.getWareHoseTypeId(), customerTypeId, shopId, orderProductsDTO))
            .collect(Collectors.toList());
        orderProductsDTO.setProducts(productDTOS);
        return new Response<OrderProductsDTO>().withData(orderProductsDTO);
    }

    private OrderProductDTO mapProductIdToProductDTO(OrderProductRequest productRequest,
        Long warehouseTypeId, Long customerTypeId, Long shopId, OrderProductsDTO orderProductsDTO) {
        Product product = repository.findById(productRequest.getProductId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductDTO dto = modelMapper.map(product, OrderProductDTO.class);
        dto.setQuantity(productRequest.getQuantity());
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());

        dto.setStockTotal(stockTotal.getQuantity());
        orderProductsDTO.addQuantity(productRequest.getQuantity());
        orderProductsDTO.addTotalPrice(dto.getTotalPrice());

        return dto;
    }


    private ProductDTO mapProductIdToProductDTO(Long productId, Long customerTypeId, Long warehouseTypeId, Long shopId) {
        Product product = repository.findById(productId).orElse(null);
        if(product != null ) return this.mapProductToProductDTO(product, customerTypeId, warehouseTypeId,  shopId);
        return null;
    }

    private ProductDTO mapProductToProductDTO(
        Product product, Long customerTypeId, Long warehouseTypeId, Long shopId) {
        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());

        dto.setStockTotal(stockTotal.getQuantity());
        return dto;
    }

    private ProductDTO mapProductToProductDTO(Product product, Long customerTypeId) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());
        return dto;
    }


}
