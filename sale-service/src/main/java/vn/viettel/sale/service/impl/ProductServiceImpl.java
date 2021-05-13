package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
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
    ProductPriceRepository productPriceRepository;
    @Autowired
    ProductPriceRepository productPriceRepo;
    @Autowired
    SaleOrderDetailRepository saleOrderDetailRepo;
    @Autowired
    StockTotalRepository stockTotalRepo;
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Autowired
    CustomerClient customerClient;
    @Override
    public Response<Page<ProductInfoDTO>> findAllProductInfo(Integer status, Integer type, Pageable pageable) {
        Page<ProductInfo> productInfos
                = productInfoRepo.findAll(Specification.where(
                ProductInfoSpecification.hasStatus(status).and(ProductInfoSpecification.hasType(type))), pageable);
        Page<ProductInfoDTO> productDTOS = productInfos.map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ProductInfoDTO dto = modelMapper.map(product, ProductInfoDTO.class);
            return dto;
        });
        return new Response<Page<ProductInfoDTO>>().withData(productDTOS);
    }
    @Override
    public Response<OrderProductDTO> getProduct(Long id, Long customerTypeId, Long shopId) {
        Product product = repository.findById(id).orElse(null);
        if (product == null)
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        OrderProductDTO productDTO = this.mapProductToProductDTO(
                product, customerTypeId, customerTypeDTO.getWareHoseTypeId(), shopId);
        return new Response<OrderProductDTO>().withData(productDTO);
    }
    @Override
    public Response<Page<OrderProductDTO>> findProducts(ProductFilter filter, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(filter.getKeyWord())
                        .and(ProductSpecification.hasProductInfo(filter.getProductInfoId()))
                        .and(ProductSpecification.hasStatus(filter.getStatus()))
                        .and(ProductSpecification.deletedAtIsNull())), pageable);
        Page<OrderProductDTO> productDTOS = products.map(
                product -> this.mapProductToProductDTO(product, filter.getCustomerTypeId()));

        return new Response<Page<OrderProductDTO>>().withData(productDTOS);
    }
    @Override
    public Response<Page<OrderProductDTO>> findProductsTopSale(Long shopId, String keyWord, Long customerTypeId, Pageable pageable) {
        String keyUpper = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        Page<BigDecimal> productIds = repository.findProductTopSale(shopId, keyWord, keyUpper, pageable);

        Page<OrderProductDTO> productDTOS = productIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), customerTypeId));

        return new Response<Page<OrderProductDTO>>().withData(productDTOS);
    }
    @Override
    public Response<Page<OrderProductDTO>> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable) {
        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(customerId).getData();
        if (customerDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Page<BigDecimal> productIds = repository.findProductsCustomerTopSale(shopId, customerId, pageable);

        Page<OrderProductDTO> productDTOS = productIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), customerDTO.getCustomerTypeId()));

        return new Response<Page<OrderProductDTO>>().withData(productDTOS);
    }
    @Override
    public Response<OrderProductsDTO> changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> productsRequest) {
        OrderProductsDTO orderProductsDTO = new OrderProductsDTO();

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductOnlineDTO> productDTOS = productsRequest.stream().map(product ->
                this.mapProductIdToProductDTO(product, customerTypeDTO.getWareHoseTypeId(), customerTypeId, shopId, orderProductsDTO))
                .collect(Collectors.toList());
        orderProductsDTO.setProducts(productDTOS);
        return new Response<OrderProductsDTO>().withData(orderProductsDTO);
    }
    @Override
    public Response<List<OrderProductDTO>> findProductsByKeyWord(String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)
                        .and(ProductSpecification.deletedAtIsNull())));
        List<OrderProductDTO> rs = products.stream().map(
                item -> modelMapper.map(item, OrderProductDTO.class)
        ).collect(Collectors.toList());
        return new Response<List<OrderProductDTO>>().withData(rs);
    }
    @Override
    public Response<List<ProductDataSearchDTO>> findAllProduct(String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)
                        .and(ProductSpecification.deletedAtIsNull())));
        if (products.isEmpty()) {
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        }
        List<ProductDataSearchDTO> rs = products.stream().map(item -> {
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    ProductDataSearchDTO dto = modelMapper.map(item, ProductDataSearchDTO.class);
                    dto.setQuantity(1);
                    Price price = productPriceRepository.getProductPriceByProductId(item.getId());
                    dto.setPrice(price.getPriceNotVat());
                    dto.setIntoMoney(price.getPriceNotVat());
                    dto.setVat(price.getVat());
                    dto.setVatAmount((price.getPriceNotVat() * price.getVat()) / 100);
                    return dto;
                }
        ).collect(Collectors.toList());
        return new Response<List<ProductDataSearchDTO>>().withData(rs);
    }
    @Override
    public Response<Page<ProductDTO>> findProduct(List<String> productCodes, String productName, Long catId, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(ProductSpecification.hasProductCode(productCodes)).and(ProductSpecification.hasProductName(productName)).and(ProductSpecification.hasCatId(catId)), pageable);
       Page<ProductDTO> productDTOS = products.map(this::mapProductToProductDTO);
        return new Response< Page<ProductDTO>>().withData(productDTOS);
    }

    @Override
    public Response<List<ProductInfoDTO>> getAllProductCat() {
        List<ProductInfo> productInfo = productInfoRepo.getAllProductInfo();
        List<ProductInfoDTO> list = productInfo.stream().map(
                item -> modelMapper.map(item, ProductInfoDTO.class)
        ).collect(Collectors.toList());
        return new Response< List<ProductInfoDTO>>().withData(list);
    }
    private OrderProductOnlineDTO mapProductIdToProductDTO(OrderProductRequest productRequest,
                                                           Long warehouseTypeId, Long customerTypeId, Long shopId, OrderProductsDTO orderProductsDTO) {
        Product product = repository.findById(productRequest.getProductId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));
        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO dto = modelMapper.map(product, OrderProductOnlineDTO.class);
        dto.setQuantity(productRequest.getQuantity());
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());
        dto.setStockTotal(stockTotal.getQuantity());
        orderProductsDTO.addQuantity(productRequest.getQuantity());
        orderProductsDTO.addTotalPrice(dto.getTotalPrice());

        return dto;
    }
    private OrderProductDTO mapProductIdToProductDTO(Long productId, Long customerTypeId) {
        Product product = repository.findById(productId).orElse(null);
        if (product != null) return mapProductToProductDTO(product, customerTypeId);
        return null;
    }
    private OrderProductDTO mapProductToProductDTO(
            Product product, Long customerTypeId, Long warehouseTypeId, Long shopId) {
        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductDTO dto = modelMapper.map(product, OrderProductDTO.class);
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());
        dto.setStockTotal(stockTotal.getQuantity());
        return dto;
    }
    private OrderProductDTO mapProductToProductDTO(Product product, Long customerTypeId) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductDTO dto = modelMapper.map(product, OrderProductDTO.class);
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
        dto.setPrice(productPrice.getPrice());
        return dto;
    }
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

}
