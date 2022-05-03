package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.repository.ProductInfoRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.dto.ProductInfoDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.impl.ProductServiceImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ProductControllerTest extends BaseTest {
    private final String root = "/sales/products";

    @InjectMocks
    ProductServiceImpl serviceImp;

    @Mock
    ProductService service;

    @Mock
    ProductRepository repository;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    StockTotalRepository stockTotalRepo;

    @Mock
    ProductPriceRepository productPriceRepo;

    @Mock
    ProductInfoRepository productInfoRepo;

    @Mock
    CustomerClient customerClient;

    @Mock
    private Clock clock;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ProductController controller = new ProductController();
        controller.setService(service);
        this.setupAction(controller);
    }

    //-------------------------------findALlProductInfo-------------------------------
    @Test
    public void findComboProductsSuccessTest() throws Exception {
        String uri = V1 + root + "/product-infos";
        Integer status = 1;
        Integer type = 1;
        Pageable pageable = PageRequest.of(0, 5);

        ProductInfo productInfo = new ProductInfo();
        List<ProductInfo> lstDto = new ArrayList<>();
        lstDto.add(productInfo);
        final Page<ProductInfo> pageData = new PageImpl<>(lstDto);
        when(productInfoRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageData);

        Page<ProductInfoDTO> result = serviceImp.findAllProductInfo(status, type, pageable);

        assertNotNull(result);

        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findProducts-------------------------------------
    @Test
    public void findProductsSuccessTest() throws Exception {
        String uri = V1 + root ;
        Pageable pageable = PageRequest.of(0, 5);
        ProductFilter filter = new ProductFilter();
        filter.setCustomerId(1L);
        filter.setKeyWord("123");
        filter.setShopId(1L);
        filter.setStatus(1);
        filter.setProductInfoId(1L);

        List<OrderProductDTO> list = Arrays.asList(new OrderProductDTO(), new OrderProductDTO());
        Page<OrderProductDTO> data = new PageImpl<>(list, pageable , list.size());
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerTypeId(1L);
        Long wareHouseTypeId = 1L;
        given(customerClient.getCustomerByIdV1(filter.getCustomerId())).willReturn(new Response<CustomerDTO>().withData(customerDTO));
        given(customerTypeClient.getWarehouseTypeByShopId(filter.getShopId())).willReturn(wareHouseTypeId);
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            given(repository.findOrderProductDTO(filter.getShopId(), customerDTO.getCustomerTypeId(), wareHouseTypeId, null,
                    filter.getKeyWord(), filter.getStatus(), filter.getProductInfoId(), LocalDateTime.now(), pageable)).willReturn(data);

            Page<OrderProductDTO> result = serviceImp.findProducts(filter, pageable);
            assertNotNull(result);
        }

        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .param("customerId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findProductsTopSale------------------------------
    @Test
    public void findProductsTopSaleSuccessTest() throws Exception {
        String uri = V1 + root + "/top-sale/month";
        Long shopId = 1L;
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerTypeId(1L);
        Long wareHouseTypeId = 1L;
        given(customerClient.getCustomerByIdV1(customerId)).willReturn(new Response<CustomerDTO>().withData(customerDTO));
        given(customerTypeClient.getWarehouseTypeByShopId(shopId)).willReturn(wareHouseTypeId);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
            LocalDateTime toDate = LocalDateTime.now();

            List<OrderProductDTO> list = Arrays.asList(new OrderProductDTO(), new OrderProductDTO());
            Page<OrderProductDTO> data = new PageImpl<>(list, pageable , list.size());
            given(repository.findOrderProductTopSale(shopId, customerDTO.getCustomerTypeId(), wareHouseTypeId, customerId,
                    "", fromDate, toDate, false, pageable)).willReturn(data);

            Page<OrderProductDTO> result = serviceImp.findProductsMonth(shopId,customerId, pageable);

            assertNotNull(result);
        }

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------findProductsCustomerTopSale----------------------
    @Test
    public void findProductsCustomerTopSaleSuccessTest() throws Exception {
        String uri = V1 + root + "/top-sale/customer/{customerId}";
        Long shopId = 1L;
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerTypeId(1L);
        Long wareHouseTypeId = 1L;
        given(customerClient.getCustomerByIdV1(customerId)).willReturn(new Response<CustomerDTO>().withData(customerDTO));
        given(customerTypeClient.getWarehouseTypeByShopId(shopId)).willReturn(wareHouseTypeId);
        List<Long> list = Arrays.asList(1L);
        Page<Long> productIds = new PageImpl<>(list, pageable , list.size());
        given(repository.findProductsCustomerTopSale(shopId, customerId, pageable)).willReturn(productIds);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            LocalDateTime fromDate = DateUtils.getFirstDayOfCurrentMonth();
            LocalDateTime toDate = LocalDateTime.now();

            List<OrderProductDTO> list1 = Arrays.asList(new OrderProductDTO(), new OrderProductDTO());
            Page<OrderProductDTO> data = new PageImpl<>(list1, pageable , list1.size());
            given(repository.findOrderProductDTO(shopId, customerDTO.getCustomerTypeId(), wareHouseTypeId, productIds.getContent(),
                    null, null, null, LocalDateTime.now(), pageable)).willReturn(data);

            Page<OrderProductDTO> result = serviceImp.findProductsCustomerTopSale(shopId,customerId, pageable);

            assertNotNull(result);
        }

        ResultActions resultActions = mockMvc.perform(get(uri,1)
                        .param("page", "5")
                        .param("size", "10")
                        .param("customerTypeId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------changeCustomerType-------------------------------
    @Test
    public void changeCustomerTypeSuccessV1Test() throws Exception {
        String uri = V1 + root + "/change/customer-type/{customerTypeId}";
        Long customerTypeId = 1L;
        Long shopId = 1L;
        List<OrderProductRequest> productsRequests = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<StockTotal> stockTotals = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            OrderProductRequest orderProductRequest = new OrderProductRequest();
            orderProductRequest.setProductId((long)i);
            productsRequests.add(orderProductRequest);

            Product product = new Product();
            product.setId((long)i);
            products.add(product);
        }
        CustomerTypeDTO customerType = new CustomerTypeDTO();
        customerType.setWareHouseTypeId(1L);
        given(customerTypeClient.getCusTypeById(customerTypeId)).willReturn(customerType);

        List<Long> productIds = productsRequests.stream().map(item -> item.getProductId()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        given(repository.getProducts(productIds,1)).willReturn(products);
        given(stockTotalRepo.getStockTotal(shopId, customerType.getWareHouseTypeId(), productIds)).willReturn(stockTotals);
        given(productPriceRepo.findProductPriceWithType(productIds, customerTypeId, DateUtils.convertToDate(LocalDateTime.now()))).willReturn(new ArrayList<>());

        OrderProductsDTO result = serviceImp.changeCustomerType(customerTypeId, shopId, productsRequests);
        assertNotNull(result);

        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------findProductsByKeyWord-----------------------------
    @Test
    public void findProductsByKeyWordSuccessTest() throws Exception {
        String uri = V1 + root + "/find";
        Long shopId = 1L;
        Long customerId = 1L;
        String keyWord = "123";
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        products.add(product);

        given(repository.findAll(any(Specification.class))).willReturn(products);

        CustomerTypeDTO customerType = new CustomerTypeDTO();
        customerType.setId(1L);
        customerType.setWareHouseTypeId(1L);
        given(customerTypeClient.getCustomerTypeForSale(customerId, shopId)).willReturn(null);
        given(customerTypeClient.getCusTypeByShopIdV1(shopId)).willReturn(customerType);

        List<Price> prices = new ArrayList<>();
        Price price = new Price();
        price.setProductId(1L);
        prices.add(price);
        given(productPriceRepo.findProductPriceWithType(products.stream().map(item -> item.getId()).
                collect(Collectors.toList()), customerType.getId(), DateUtils.convertToDate(LocalDateTime.now()))).willReturn(prices);

        List<OrderProductDTO> result = serviceImp.findProductsByKeyWord(shopId, customerId, keyWord);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------Choose-product------------------------------------
    @Test
    public void findSuccessTest() throws Exception {
        String uri = V1 + root+ "/choose-product";
        List<Product> lstDto = new ArrayList<>();
        lstDto.add(new Product());
        lstDto.add(new Product());

        Long shopId = 1L;
        String productCode = "123";
        String productName = "123";
        Long catId = 1L;
        Pageable pageable = PageRequest.of(0,5);
        Page<Product> pageDto = new PageImpl<>(lstDto, pageable, lstDto.size());
        given(repository.findAll(any(Specification.class),any(Pageable.class))).willReturn(pageDto);

        Page<ProductDTO> result = serviceImp.findProduct(shopId,productCode,productName,catId, pageable);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
