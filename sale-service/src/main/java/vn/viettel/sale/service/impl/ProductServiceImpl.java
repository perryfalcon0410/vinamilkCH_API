package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.*;
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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
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
    MediaItemRepository mediaItemRepo;
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Autowired
    CustomerClient customerClient;
    @Override
    public Page<ProductInfoDTO> findAllProductInfo(Integer status, Integer type, Pageable pageable) {
        Page<ProductInfo> productInfos
                = productInfoRepo.findAll(Specification.where(
                ProductInfoSpecification.hasStatus(status).and(ProductInfoSpecification.hasType(type))), pageable);
        Page<ProductInfoDTO> productDTOS = productInfos.map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ProductInfoDTO dto = modelMapper.map(product, ProductInfoDTO.class);
            return dto;
        });
        return productDTOS;
    }
    @Override
    public OrderProductDTO getProduct(Long id, Long customerTypeId, Long shopId) {
        Product product = repository.findById(id).orElse(null);
        if (product == null)
            throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        OrderProductDTO productDTO = this.mapProductToProductDTO(
                product, customerTypeId, customerTypeDTO.getWareHouseTypeId(), shopId);
        return productDTO;
    }
    @Override
    public Page<OrderProductDTO> findProducts(ProductFilter filter, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(filter.getKeyWord())
                        .and(ProductSpecification.hasProductInfo(filter.getProductInfoId()))
                        .and(ProductSpecification.hasStatus(filter.getStatus()))
        ), pageable);
        Page<OrderProductDTO> productDTOS = products.map(
                product -> this.mapProductToProductDTO(product, filter.getCustomerTypeId()));

        return productDTOS;
    }
    @Override
    public Page<OrderProductDTO> findProductsTopSale(Long shopId, String keyWord, Long customerTypeId, Integer checkStocktotal, Pageable pageable) {
        String keyUpper = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -6);
        Timestamp fromDate = DateUtils.convertFromDate(this.getFirstDateOfMonth(c.getTime()));
        Timestamp toDate = DateUtils.convertToDate(referenceDate);
        Page<BigDecimal> productIds = null;
        if(checkStocktotal == 1) {
            CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
            productIds = repository.topSaleAndCheckStockTotal(shopId, customerType.getWareHouseTypeId(), keyWord, keyUpper,fromDate, toDate, pageable);
        }
        if(checkStocktotal == 0)
            productIds = repository.findProductsTopSale(shopId, keyWord, keyUpper,fromDate, toDate, pageable);

        Page<OrderProductDTO> productDTOS = productIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), customerTypeId));
        return productDTOS;
    }

    @Override
    public Page<OrderProductDTO> findProductsMonth(Long shopId, Long customerTypeId, Pageable pageable) {
        Timestamp fromDate = DateUtils.convertFromDate(this.getFirstDateOfMonth(new Date()));
        Timestamp toDate = DateUtils.convertToDate(new Date());
        Page<BigDecimal> productIds = repository.findProductsTopSale(shopId, "", "", fromDate, toDate, pageable);
        return productIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), customerTypeId));
    }

    @Override
    public Page<OrderProductDTO> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable) {
        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(customerId).getData();
        if (customerDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        Page<BigDecimal> productIds = repository.findProductsCustomerTopSale(shopId, customerId, pageable);

        Page<OrderProductDTO> productDTOS = productIds.map(id -> this.mapProductIdToProductDTO(id.longValue(), customerDTO.getCustomerTypeId()));

        return productDTOS;
    }
    @Override
    public OrderProductsDTO changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> productsRequest) {
        OrderProductsDTO orderProductsDTO = new OrderProductsDTO();

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductOnlineDTO> productDTOS = productsRequest.stream().map(product ->
                this.mapProductIdToProductDTO(product, customerTypeDTO.getWareHouseTypeId(), customerTypeId, shopId, orderProductsDTO))
                .collect(Collectors.toList());
        orderProductsDTO.setProducts(productDTOS);
        return orderProductsDTO;
    }
    @Override
    public Response<List<OrderProductDTO>> findProductsByKeyWord(String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)));
        List<OrderProductDTO> rs = products.stream().map(
                item -> modelMapper.map(item, OrderProductDTO.class)
        ).collect(Collectors.toList());
        return new Response<List<OrderProductDTO>>().withData(rs);
    }
    @Override
    public List<ProductDataSearchDTO> findAllProduct(String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)));
        List<ProductDataSearchDTO> rs = products.stream().map(item -> {
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    ProductDataSearchDTO dto = modelMapper.map(item, ProductDataSearchDTO.class);
                    dto.setQuantity(1);
                    Price price = productPriceRepository.getProductPriceByProductId(item.getId());
                    dto.setPrice(price.getPriceNotVat());
                    dto.setIntoMoney(price.getPriceNotVat());
                    dto.setVat(price.getVat());
                    dto.setVatAmount((price.getPriceNotVat() * price.getVat()) / 100);
                    dto.setNote("OT1");
                    return dto;
                }
        ).collect(Collectors.toList());
        return rs;
    }
    @Override
    public Response<Page<ProductDTO>> findProduct(String productCodes, String productName, Long catId,Pageable pageable) {
        String [] productSplit = null;
        if(productCodes!=null){
            productSplit = productCodes.split(",");
        }
        Page<Product> products = repository.findAll(Specification.where(ProductSpecification.hasProductCode(productSplit)).or(ProductSpecification.hasProductName(productName)).and(ProductSpecification.hasCatId(catId)),pageable);
        Page<ProductDTO> productDTOS = products.map(this::mapProductToProductDTO2);
        return new Response<Page<ProductDTO>>().withData(productDTOS);
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
        dto.setProductId(product.getId());
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
        return mapProductToProductDTO(product, customerTypeId);
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
        MediaItem mediaItem = mediaItemRepo.getImageProduct(product.getId()).orElse(null);
        if(mediaItem!=null && mediaItem.getUrl() != null){
            String url = mediaItem.getUrl();
            dto.setImage(url.substring(url.lastIndexOf('/')+1).trim());
        }
        if (productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
        dto.setPrice(productPrice.getPrice());
        return dto;
    }

    private ProductDTO mapProductToProductDTO2(Product product) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        return dto;
    }

    private Date getFirstDateOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

}
