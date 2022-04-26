package vn.viettel.sale.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.SalePromotionDTO;
import vn.viettel.sale.service.feign.*;

import javax.persistence.EntityManager;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class SaleServiceImplTest extends BaseTest {
    @InjectMocks
    SaleServiceImpl service;

    @Mock
    SaleOrderRepository repository;

    @Mock
    public EntityManager entityManager;

    @Mock
    SaleOrderDetailRepository detailRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    StockTotalRepository stockTotalRepository;
    @Mock
    ProductPriceRepository priceRepository;
    @Mock
    ComboProductRepository comboProductRepository;
    @Mock
    ComboProductDetailRepository comboDetailRepository;
    @Mock
    SaleOrderComboDetailRepository saleOrderComboDetailRepo;
    @Mock
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Mock
    OnlineOrderRepository onlineOrderRepo;
    @Mock
    OnlineOrderDetailRepository onlineOrderDetailRepo;
    @Mock
    CustomerClient customerClient;
    @Mock
    CustomerTypeClient customerTypeClient;
    @Mock
    PromotionClient promotionClient;
    @Mock
    ApparamClient apparamClient;
    @Mock
    ShopClient shopClient;
    @Mock
    OnlineOrderService onlineOrderService;
    @Mock
    SalePromotionService salePromotionService;
    @Mock
    SaleOrderDiscountRepository saleOrderDiscountRepo;
    @Mock
    SaleOrderComboDiscountRepository saleOrderComboDiscountRepo;
    @Mock
    SaleOrderService saleOrderService;

    @Mock
    StockTotalService stockTotalService;
    SaleOrderRequest request = new SaleOrderRequest();
    long userId = 1L;
    long shopId = 1L;
    boolean printTemp = true;
    CustomerDTO customer = new CustomerDTO();

    @Before
    public void setUp() throws Exception {
        List<SalePromotionDTO> salePromotionDTOS = new ArrayList<>();
        request.setPromotionInfo(salePromotionDTOS);
        List<ProductOrderRequest> products = new ArrayList<>();
        ProductOrderRequest productOrderRequest = new ProductOrderRequest();
        productOrderRequest.setProductId(1L);
        productOrderRequest.setQuantity(5);

        products.add(productOrderRequest);
        request.setProducts(products);
        request.setCustomerId(1L);
        request.setOrderType(1);

//        customer.setCustomerTypeId(1L);
//        Response<CustomerDTO> response = new Response<>();
//        response.setData(customer);
//        Mockito.when(customerClient.getCustomerByIdV1(request.getCustomerId())).thenReturn(response);
//
//        ShopDTO shop = new ShopDTO();
//        Response<ShopDTO> response1 = new Response<>();
//        response1.setData(shop);
//        Mockito.when(shopClient.getByIdV1(shopId)).thenReturn(response1);
//
//        ApParamDTO apParamDTO = new ApParamDTO();
//        Response<ApParamDTO> response2 = new Response<>();
//        response2.setData(apParamDTO);
//        Mockito.when(apparamClient.getApParamByTypeAndvalue("", request.getOrderType().toString()))
//                .thenReturn(response2);
//
//        CustomerTypeDTO customerType = new CustomerTypeDTO();
//        customerType.setWareHouseTypeId(1L);
//        Mockito.when(customerTypeClient.getCustomerTypeForSale(request.getCustomerId(), shopId)).thenReturn(customerType);
//
//        HashMap<Long, ProductOrderRequest> mapProductOrder = new HashMap<>();
//        mapProductOrder.put(request.getProducts().get(0).getProductId(), request.getProducts().get(0));
//        List<Long> productNotAccumulated = new ArrayList<>();
//        Response response3 = new Response<>();
//        response3.setData(productNotAccumulated);
//        Mockito.when(promotionClient.getProductsNotAccumulatedV1(new ArrayList<>(mapProductOrder.keySet()))).thenReturn(response3);
//
//        List<ProductOrderRequest> lstProductOrder = new ArrayList<>(mapProductOrder.values());
//        List<Price> productPrices = new ArrayList<>();
//        Mockito.when(priceRepository.findProductPriceWithType(lstProductOrder.stream().map(i -> i.getProductId()).collect(Collectors.toList()),
//                customer.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()))).thenReturn(productPrices);
//
//        HashMap<Long, Integer> mapProductWithQty = new HashMap<>();
//        mapProductWithQty.put(productOrderRequest.getProductId(), productOrderRequest.getQuantity());
//        List<FreeProductDTO> freeProductDTOs = new ArrayList<>();
//        FreeProductDTO freeProductDTO = new FreeProductDTO();
//        freeProductDTO.setStockQuantity(50);
//        freeProductDTOs.add(freeProductDTO);
//        when(productRepository.findProductWithStock(shopId, customerType.getWareHouseTypeId(),
//                new ArrayList<>(mapProductWithQty.keySet()))).thenReturn(freeProductDTOs);
    }

    @Test
    public void createSaleOrder() {

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);



//            service.createSaleOrder(request, userId, shopId, printTemp);
        }


    }

    @Test
    public void updateRPTZV23() {
    }

    @Test
    public void updateAccumulatedAmount() {
    }

    @Test
    public void updateStockTotal() {
    }

    @Test
    public void createOrderNumber() {
    }

    @Test
    public void getValidOnlineOrder() {
    }

    @Test
    public void updateCustomer() {
    }

    @Test
    public void withLargeIntegers() {
    }

    @Test
    public void safeSave() {
    }

    @Test
    public void removeSafeSaleOrder() {
    }
}