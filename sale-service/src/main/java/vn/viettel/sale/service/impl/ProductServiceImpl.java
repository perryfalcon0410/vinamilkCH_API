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
    public Page<OrderProductDTO> findProducts(ProductFilter filter, Pageable pageable) {
        CustomerDTO customer = customerClient.getCustomerByIdV1(filter.getCustomerId()).getData();
        if (customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        String nameLowerCase = null;
        if (filter.getKeyWord() != null) {
            nameLowerCase = VNCharacterUtils.removeAccent(filter.getKeyWord()).toUpperCase(Locale.ROOT);
        }
        Long wareHouseTypeId = customerTypeClient.getWarehouseTypeByShopId(filter.getShopId());
        if (wareHouseTypeId == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        return repository.findOrderProductDTO(filter.getShopId(), customer.getCustomerTypeId(), wareHouseTypeId, null,
                nameLowerCase, filter.getStatus(), filter.getProductInfoId(), LocalDateTime.now(), pageable);
    }

    @Override
    public Page<OrderProductDTO> findProductsTopSale(Long shopId, String keyWord, Long customerId, Integer checkStocktotal, Pageable pageable) {
        if (keyWord != null)
            keyWord = VNCharacterUtils.removeAccent(keyWord.trim()).toUpperCase(Locale.ROOT);

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime toDate = DateUtils.convertToDate(localDateTime);
        LocalDateTime fromDate = DateUtils.convertFromDate(localDateTime.plusMonths(-6));
        boolean hasQty = false;
        CustomerTypeDTO customerType = getCustomerType(shopId, customerId);

        if (checkStocktotal != null && checkStocktotal == 1) hasQty = true;
        return repository.findOrderProductTopSale(shopId, customerType.getId(), customerType.getWareHouseTypeId(), customerId,
                keyWord, fromDate, toDate, hasQty, pageable);
    }

    @Override
    public List<FreeProductDTO> findFreeProductDTONoOrder(Long shopId, Long warehouseId, String keyWord, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("productCode").and(Sort.by("productName")));
        String keyUpper = "";
        if (keyWord != null) {
            keyUpper = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
        }
        if (warehouseId == null) {
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
        if (customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        Long wareHouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (wareHouseTypeId == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
        LocalDateTime toDate = LocalDateTime.now();
//        Page<Long> productIds = repository.findProductsTopSale(shopId, customerId, null, "", fromDate, toDate, false, pageable);

        return repository.findOrderProductTopSale(shopId, customer.getCustomerTypeId(), wareHouseTypeId, customerId,
                "", fromDate, toDate, false, pageable);
    }

    @Override
    public Page<OrderProductDTO> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable) {
        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(customerId).getData();
        if (customerDTO == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        Long wareHouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (wareHouseTypeId == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);

        Page<Long> productIds = repository.findProductsCustomerTopSale(shopId, customerId, pageable);

        return repository.findOrderProductDTO(shopId, customerDTO.getCustomerTypeId(), wareHouseTypeId, productIds.getContent(),
                null, null, null, LocalDateTime.now(), pageable);
    }

    @Override
    public OrderProductsDTO changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> productsRequest) {
        OrderProductsDTO orderProductsDTO = new OrderProductsDTO();
        CustomerTypeDTO customerType = customerTypeClient.getCusTypeById(customerTypeId);
        if (customerType == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
        List<Long> productIds = productsRequest.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
        List<Product> products = repository.getProducts(productIds,1);
        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, customerType.getWareHouseTypeId(), productIds);
        List<Price> prices = productPriceRepo.findProductPriceWithType(productIds, customerTypeId, DateUtils.convertToDate(LocalDateTime.now()));

        List<OrderProductOnlineDTO> productDTOS = productsRequest.stream().map(product ->
                this.mapProductIdToProductDTO(product, products, prices, stockTotals, orderProductsDTO))
                .collect(Collectors.toList());
        orderProductsDTO.setProducts(productDTOS);
        return orderProductsDTO;
    }

    @Override
    public List<OrderProductDTO> findProductsByKeyWord(Long shopId, Long customerId, String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)));
        CustomerTypeDTO customerType = getCustomerType(shopId, customerId);
        List<OrderProductDTO> rp = new ArrayList<>();

        if(products!=null && !products.isEmpty()){
            List<Price> prices = productPriceRepo.findProductPriceWithType(products.stream().map(item -> item.getId()).
                    collect(Collectors.toList()), customerType.getId(), DateUtils.convertToDate(LocalDateTime.now()));
            return products.stream().map(item -> {
                        OrderProductDTO dto = modelMapper.map(item, OrderProductDTO.class);
                        if(prices != null){
                            for(Price price : prices){
                                if(price.getProductId().equals(item.getId())){
                                    dto.setPrice(price.getPrice());
                                    break;
                                }
                            }
                        }
                        return dto;
                    }
            ).collect(Collectors.toList());
        }else return rp;

    }

    @Override
    public List<ProductDataSearchDTO> findAllProduct(Long shopId, String keyWord) {
        List<Product> products = repository.findAll(Specification.where(
                ProductSpecification.hasCodeOrName(keyWord)), Sort.by(Sort.Direction.ASC, "productCode"));
        if(products.size() > 0) {
            List<Price> prices = productPriceRepo.findProductPrice(products.stream().map(item -> item.getId()).collect(Collectors.toList()), 1, LocalDateTime.now());
            return products.stream().map(item -> {
                        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                        ProductDataSearchDTO dto = modelMapper.map(item, ProductDataSearchDTO.class);
                        dto.setQuantity(1);
                        dto.setPrice((double) 0);
                        dto.setIntoMoney((double) 0);
                        dto.setVat((double) 0);
                        dto.setVatAmount((double) 0);
                        if(prices != null){
                            for(Price price : prices){
                                if(price.getProductId().equals(item.getId())){
                                    dto.setPrice(price.getPriceNotVat());
                                    dto.setIntoMoney(price.getPriceNotVat());
                                    dto.setVat(price.getVat());
                                    dto.setVatAmount(roundValue((price.getPriceNotVat() * price.getVat()) / 100));
                                    break;
                                }
                            }
                        }
                        dto.setNote("OT1");
                        return dto;
                    }
            ).collect(Collectors.toList());
        }throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND);
    }

    @Override
    public Page<ProductDTO> findProduct(Long shopId, String productCode, String productName, Long catId, Pageable pageable) {
        Page<Product> products = repository.findAll(Specification.where(ProductSpecification.hasProductCode(productCode)
                .and(ProductSpecification.hasProductName(productName).and(ProductSpecification.hasCatId(catId)
                        .and(ProductSpecification.hasStatus())))), pageable);
        return products.map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public Response<List<ProductInfoDTO>> getAllProductCat() {
        List<ProductInfo> productInfo = productInfoRepo.getAllProductInfo();
        List<ProductInfoDTO> list = productInfo.stream().map(
                item -> modelMapper.map(item, ProductInfoDTO.class)
        ).collect(Collectors.toList());
        return new Response<List<ProductInfoDTO>>().withData(list);
    }

    @Override
    public PriceDTO getProductPriceById(Long shopId, Long productId) {
        List<Price> prices = productPriceRepo.findProductPrice(Arrays.asList(productId), 1, LocalDateTime.now());
        if(prices == null || prices.isEmpty()) return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PriceDTO dto = modelMapper.map(prices.get(0), PriceDTO.class);
        return dto;
    }

    private CustomerTypeDTO getCustomerType(Long shopId, Long customerId){
        CustomerTypeDTO customerType = null;
        if(customerId != null) customerType = customerTypeClient.getCusTypeByCustomerIdV1(customerId);
        if(customerType == null) customerType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        return customerType;
    }

    @Override
    public OrderProductDTO getByBarcode(Long shopId, String barcode, Long customerId) {
        CustomerTypeDTO customerType = getCustomerType(shopId, customerId);

        List<Product> products = repository.getByBarCodeAndStatus(barcode, 1);
        if(products == null || products.isEmpty()) return null;
        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, customerType.getWareHouseTypeId(), products.get(0).getId());

        List<Price> prices = productPriceRepo.findProductPriceWithType(Arrays.asList(products.get(0).getId()), customerType.getId(), DateUtils.convertToDate(LocalDateTime.now()));
        if(prices == null || prices.isEmpty() ) return null;

        OrderProductDTO orderProductDTO = modelMapper.map(products.get(0), OrderProductDTO.class);
        orderProductDTO.setPrice(prices.get(0).getPrice());
        if(stockTotals == null || stockTotals.isEmpty())
            orderProductDTO.setStockTotal(stockTotals.get(0).getQuantity());

        return orderProductDTO;
    }

    private OrderProductOnlineDTO mapProductIdToProductDTO(OrderProductRequest productRequest, List<Product> products, List<Price> prices,
                                                           List<StockTotal> stockTotals, OrderProductsDTO orderProductsDTO) {
        Product product = null;
        for (Product p : products){
            if(p.getId().equals(productRequest.getProductId())){
                product = p; break;
            }
        }
        if(product == null) throw new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND, productRequest.getProductId() + "");

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO dto = modelMapper.map(product, OrderProductOnlineDTO.class);
        dto.setProductId(product.getId());
        dto.setQuantity(productRequest.getQuantity());

        Price price = null;
        for (Price p : prices){
            if(p.getProductId().equals(productRequest.getProductId())){
                price = p; break;
            }
        }
        if(price == null) throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED, product.getProductCode());

        dto.setPrice(price.getPrice());
        StockTotal stockTotal = null;
        for(StockTotal p : stockTotals){
            if(p.getProductId().equals(product.getId())){
                stockTotal = p; break;
            }
        }
        if(stockTotal == null) throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, product.getProductCode());
        dto.setStockTotal(stockTotal.getQuantity());
        orderProductsDTO.addQuantity(productRequest.getQuantity());
        orderProductsDTO.addTotalPrice(dto.getTotalPrice());

        return dto;
    }

    private double roundValue(Double value){
        if(value == null) return 0;
        return Math.round(value);
    }
}
