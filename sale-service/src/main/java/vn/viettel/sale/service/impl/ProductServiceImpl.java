package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    public OrderProductDTO getProduct(Long productId, Long customerTypeId, Long shopId) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if (customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        Pageable pageable = PageRequest.of(0, 1);
        Page<OrderProductDTO> lst = repository.findOrderProductDTO(shopId, null, customerTypeDTO.getWareHouseTypeId(), Arrays.asList(productId),
                null, null, null,LocalDateTime.now(), pageable);
        if(lst != null) return lst.getContent().get(0);
        return null;
    }

    @Override
    public Page<OrderProductDTO> findProducts(ProductFilter filter, Pageable pageable) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(filter.getCustomerId()).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        String nameLowerCase = null;
        if (filter.getKeyWord() != null) {
            nameLowerCase = VNCharacterUtils.removeAccent(filter.getKeyWord()).toUpperCase(Locale.ROOT);
        }

        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(filter.getShopId());
        if(customerType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        return repository.findOrderProductDTO(filter.getShopId(), customer.getCustomerTypeId(), customerType.getWareHouseTypeId(), null,
                nameLowerCase, filter.getStatus(), filter.getProductInfoId(),LocalDateTime.now(), pageable);
    }

    @Override
    public Page<OrderProductDTO> findProductsTopSale(Long shopId, String keyWord, Long customerId, Integer checkStocktotal, Pageable pageable) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
            if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        String keyUpper = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime toDate = DateUtils.convertToDate(localDateTime);
        LocalDateTime fromDate = DateUtils.convertFromDate(localDateTime.plusMonths(-6));
        boolean hasQty = false;
        Long warehouseTypeId = null;
        if(checkStocktotal != null && checkStocktotal == 1) {
            hasQty = true;
            CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
            warehouseTypeId = customerType.getWareHouseTypeId();
        }
        Page<Long> productIds = repository.findProductsTopSale(shopId, customerId, warehouseTypeId, keyUpper,  fromDate, toDate, hasQty, pageable);

        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        return repository.findOrderProductDTO(shopId, customer.getCustomerTypeId(), warehouseTypeId, productIds.getContent(),
                null, null, null,LocalDateTime.now(), pageable);
    }

    @Override
    public List<FreeProductDTO> findFreeProductDTONoOrder(Long shopId, Long warehouseId, String keyWord, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("productCode").and(Sort.by("productName")));
        String keyUpper = "";
        if (keyWord != null){
            keyUpper = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        }
        if(warehouseId == null) {
            CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
            if (customerType != null)
                warehouseId = customerType.getWareHouseTypeId();
        }

        Page<FreeProductDTO> result = repository.findFreeProductDTONoOrder(shopId, warehouseId, keyUpper, pageable);

        return result.getContent();
    }

    @Override
    public Page<OrderProductDTO> findProductsMonth(Long shopId, Long customerId, Pageable pageable) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
        LocalDateTime toDate = LocalDateTime.now();
        Page<Long> productIds = repository.findProductsTopSale(shopId, customerId, null, "",  fromDate, toDate, false, pageable);

        return repository.findOrderProductDTO(shopId, customer.getCustomerTypeId(), customerType.getWareHouseTypeId(), productIds.getContent(),
                null, null, null,LocalDateTime.now(), pageable);
    }
    
    @Override
    public Page<OrderProductDTO> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable) {
        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(customerId).getData();
        if(customerDTO == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        CustomerTypeDTO customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        Page<Long> productIds = repository.findProductsCustomerTopSale(shopId, customerId, pageable);

        return repository.findOrderProductDTO(shopId, customerDTO.getCustomerTypeId(), customerType.getWareHouseTypeId(), productIds.getContent(),
                null, null, null,LocalDateTime.now(), pageable);
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
        return products.stream().map(item -> {
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
    }
    @Override
    public Page<ProductDTO> findProduct(String productCodes, String productName, Long catId,Pageable pageable) {
        String [] productSplit;
        List<ProductDTO> rs;
        List<Product> listProduct1 = new ArrayList<>();
        if(productCodes!=null) {
            productSplit = productCodes.toUpperCase().split(",");
            listProduct1 = repository.findAll(Specification.where(ProductSpecification.hasProductCode(productSplit)).and(ProductSpecification.hasCatId(catId)));
        }else if(productName==null){
            listProduct1 = repository.findAll(Specification.where(ProductSpecification.hasCatId(catId)));
        }
        if(productName != null){
            List<Product> listProduct2 = repository.findAll(Specification.where(ProductSpecification.hasProductName(productName)).and(ProductSpecification.hasCatId(catId)));
            for(Product p : listProduct2){
                if(!listProduct1.contains(p)) listProduct1.add(p);
            }
        }
        List<ProductDTO> subList = listProduct1.stream().map(item->modelMapper.map(item,ProductDTO.class)).collect(Collectors.toList());
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), subList.size());
        rs = subList.subList(start, end);
        Page<ProductDTO> pageResponse = new PageImpl<>(rs,pageable,listProduct1.size());
        return pageResponse;
    }

    @Override
    public Response<List<ProductInfoDTO>> getAllProductCat() {
        List<ProductInfo> productInfo = productInfoRepo.getAllProductInfo();
        List<ProductInfoDTO> list = productInfo.stream().map(
                item -> modelMapper.map(item, ProductInfoDTO.class)
        ).collect(Collectors.toList());
        return new Response< List<ProductInfoDTO>>().withData(list);
    }

    @Override
    public PriceDTO getProductPriceById(Long productId) {
        Price prices = productPriceRepo.getProductPriceByProductId(productId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PriceDTO dto = modelMapper.map(prices, PriceDTO.class);
        return dto;
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
        List<Price> productPrices = productPriceRepo.findProductPrice(Arrays.asList(product.getId()), customerTypeId, LocalDateTime.now());
        if (productPrices != null && !productPrices.isEmpty()) dto.setPrice(productPrices.get(0).getPrice());
        dto.setStockTotal(stockTotal.getQuantity());
        orderProductsDTO.addQuantity(productRequest.getQuantity());
        orderProductsDTO.addTotalPrice(dto.getTotalPrice());

        return dto;
    }

    /*private OrderProductDTO mapProductToProductDTO(
            Product product, Long customerTypeId, Long warehouseTypeId, Long shopId) {
        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductDTO dto = modelMapper.map(product, OrderProductDTO.class);

        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if (productPrice != null) dto.setPrice(productPrice.getPrice());
        dto.setStockTotal(stockTotal.getQuantity());

        MediaItem mediaItem = mediaItemRepo.getImageProduct(product.getId()).orElse(null);
        if(mediaItem!=null && mediaItem.getUrl() != null){
            String url = mediaItem.getUrl();
            dto.setImage(url.substring(url.lastIndexOf('/')+1).trim());
        }
        return dto;
    }*/

}
