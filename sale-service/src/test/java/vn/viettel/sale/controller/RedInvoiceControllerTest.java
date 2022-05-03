package vn.viettel.sale.controller;

import liquibase.pro.packaged.gi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.RedInvoiceRequest;
import vn.viettel.sale.messaging.TotalRedInvoice;
import vn.viettel.sale.messaging.TotalRedInvoiceResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.service.impl.ProductServiceImpl;
import vn.viettel.sale.service.impl.RedInvoiceServiceImpl;
import vn.viettel.sale.service.impl.SaleOrderServiceImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedInvoiceControllerTest extends BaseTest {
    private final String root = "/sales/red-invoices";

    @InjectMocks
    RedInvoiceServiceImpl serviceImp;

    @Mock
    RedInvoiceService service;

    @Mock
    RedInvoiceRepository repository;

    @Mock
    SaleOrderService saleOrderService;

    @InjectMocks
    SaleOrderServiceImpl saleOrderServiceImpl;

    @Mock
    ProductService productService;

    @InjectMocks
    ProductServiceImpl productServiceImpl;

    @Mock
    ProductRepository productRepository;

    @Mock
    CustomerClient customerClient;

    @Mock
    RedInvoiceRepository redInvoiceRepository;

    @Mock
    RedInvoiceDetailRepository redInvoiceDetailRepository;

    @Mock
    UserClient userClient;

    @Mock
    RedInvoiceDetailService redInvoiceDetailService;

    @Mock
    SaleOrderRepository saleOrderRepository;

    @Mock
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Mock
    ProductPriceRepository productPriceRepository;

    @Mock
    ShopClient shopClient;

    @Mock
    HDDTExcelRepository hddtExcelRepository;

    @Mock
    CTDVKHRepository ctdvkhRepository;

    @Mock
    ProductPriceRepository productPriceRepo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        saleOrderServiceImpl.setModelMapper(this.modelMapper);
        productServiceImpl.setModelMapper(this.modelMapper);
        productServiceImpl.setRepository(productRepository);
        final RedInvoiceController controller = new RedInvoiceController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void findALlProductInfoTest() throws Exception {
        String uri = V1 + root ;
        String searchKeywords = "123";
        Date fromDate = new Date();
        Date toDate = new Date();
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        Long shopId = 1L;
        String invoiceNumber = "INVOICENO123";
        List<Long> ids = Arrays.asList(-1L);
        Response res = new Response();
        res.withData(ids);
        Pageable pageable = PageRequest.of(1, 5);
        List<RedInvoice> redInvoices = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            RedInvoice redInvoice = new RedInvoice();
            redInvoices.add(redInvoice);
        }
        Page<RedInvoice> pageRedInvoices = new PageImpl<RedInvoice>(redInvoices, pageable, redInvoices.size());

        given(customerClient.getIdCustomerBySearchKeyWordsV1(searchKeywords.trim())).willReturn(res);
        given(repository.findAll(any(Specification.class), any(Pageable.class))).willReturn(pageRedInvoices);
        TotalRedInvoice total = new TotalRedInvoice();
        total.setSumAmountNotVat(2.0);
        total.setSumAmountGTGT(0.0);
        given(repository.getTotalRedInvoice1(shopId, ids, invoiceNumber, tsFromDate, tsToDate)).willReturn(total);
        given(repository.getTotalRedInvoice2(shopId, ids, invoiceNumber, tsFromDate, tsToDate)).willReturn(total);

        serviceImp.getAll(shopId, searchKeywords, fromDate, toDate, invoiceNumber, pageable);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "2022/02/22")
                .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAllBillOfSaleList() throws Exception {
        String uri = V1 + root + "/bill-of-sale-list";
        int size = 2;
        int page = 5;
        RedInvoiceFilter redInvoiceFilter = new RedInvoiceFilter();
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(page, size);
        redInvoiceFilter.setSearchKeywords("123");
        redInvoiceFilter.setOrderNumber("456");
        List<Long> customerIds = Arrays.asList(1L);
        Response response = new Response();
        response.setData(customerIds);
        given(customerClient.getIdCustomerBySearchKeyWordsV1(redInvoiceFilter.getSearchKeywords().trim())).willReturn(response);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            LocalDateTime fromDate = redInvoiceFilter.getFromDate();
            LocalDateTime toDate = redInvoiceFilter.getToDate();
            if(fromDate == null) fromDate = DateUtils.getFirstDayOfCurrentMonth();
            if(toDate == null) toDate = LocalDateTime.now();

            List<SaleOrder> list1 = new ArrayList<>();
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.setId(1L);
            saleOrder.setCustomerId(1L);
            saleOrder.setMemberCardAmount(5.0);
            saleOrder.setTotal(12.0);
            list1.add(saleOrder);
            Page<SaleOrder> saleOrders = new PageImpl<>(list1, pageable , list1.size());
            given(saleOrderRepository.getAllBillOfSaleList(shopId,redInvoiceFilter.getOrderNumber(),
                    customerIds, fromDate, toDate, pageable)).willReturn(saleOrders);

            List<CustomerDTO> customers = new ArrayList<>();
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(1L);
            customers.add(customerDTO);
            given(customerClient.getCustomerInfoV1(new ArrayList<>(), null, saleOrders.stream().map(item -> item.getCustomerId())
                    .distinct().collect(Collectors.toList()))).willReturn(customers);

            Page<SaleOrderDTO> result = saleOrderServiceImpl.getAllBillOfSaleList(redInvoiceFilter, shopId, pageable);

//            assertNotNull(result);
        }

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDataInBillOfSale() throws Exception {
        String uri = V1 + root + "/show-invoice-details";

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            List<String> orderCodeList = Arrays.asList("123");
            Long shopId = 1L;
            List<Long> idCustomerList = Arrays.asList(1L, 1L);
            given(saleOrderRepository.getCustomerCode(orderCodeList)).willReturn(idCustomerList);

            List<Product> productList = new ArrayList<>();
            Product product = new Product();
            product.setId(1L);
            product.setConvFact(1);
            productList.add(product);
            given(productRepository.findAllByStatus(1)).willReturn(productList);

            List<Price> priceList = new ArrayList<>();
            Price price = new Price();
            price.setId(1L);
            price.setProductId(1L);
            price.setPrice(2.0);
            price.setPriceNotVat(1.0);
            price.setVat(1.0);
            priceList.add(price);
            given(productPriceRepository.findProductPrice(null, 1, LocalDateTime.now())).willReturn(priceList);
            Long idCus = 1L;
            SaleOrder saleOrders = new SaleOrder();
            saleOrders.setId(1L);
            for (String saleOrderCode : orderCodeList) {
                given(saleOrderRepository.findSaleOrderByCustomerIdAndOrderNumberAndType(idCus, saleOrderCode, 1)).willReturn(saleOrders);
            }
            CustomerDTO customerDTO = new CustomerDTO();
            Response response = new Response<CustomerDTO>();
            response.setData(customerDTO);
            given(customerClient.getCustomerByIdV1(idCus)).willReturn(response);

            List<SaleOrderDetail> saleOrderDetailsList = new ArrayList<>();
            SaleOrderDetail detail = new SaleOrderDetail();
            detail.setProductId(1L);
            detail.setQuantity(1);
            saleOrderDetailsList.add(detail);
            given(saleOrderDetailRepository.findSaleOrderDetail(saleOrders.getId(), false)).willReturn(saleOrderDetailsList);

            serviceImp.getDataInBillOfSale(orderCodeList, shopId);
        }

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAllProductByOrderNumber() throws Exception {
        String uri = V1 + root + "/show-info-product";
        List<ProductDetailDTO> productDetailDTOS = Arrays.asList(new ProductDetailDTO(), new ProductDetailDTO());
        given(productRepository.findProductDetailDTO("orderCode")).willReturn(productDetailDTOS);
        serviceImp.getAllProductByOrderNumber("orderCode");
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createTest() throws Exception {
        String uri = V1 + root + "/create";
        Long userId = 1L;
        Long shopId = 1L;
        RedInvoiceNewDataDTO request = new RedInvoiceNewDataDTO();
        request.setShopId(1L);
        request.setCustomerId(1L);
        request.setRedInvoiceNumber("NB001");
        request.setOfficeAddress("Tp.HCM");
        request.setTaxCode("TAX_CODE");

        List<ProductDataDTO> productDataDTOS = new ArrayList<>();
        ProductDataDTO productDataDTO = new ProductDataDTO();
        productDataDTO.setQuantity(Float.parseFloat("5"));
        productDataDTO.setPriceNotVat(Float.parseFloat("50"));
        productDataDTO.setVat(1);
        productDataDTO.setGroupVat("groupvat");
        productDataDTO.setProductId(1L);
        productDataDTOS.add(productDataDTO);
        request.setProductDataDTOS(productDataDTOS);

        List<Long> saleOrderId = Arrays.asList(1L);
        request.setSaleOrderId(saleOrderId);

        List<SaleOrder> saleOrders = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setOrderNumber("147");
        saleOrders.add(saleOrder);
        given(saleOrderRepository.findAllById(request.getSaleOrderId(), shopId)).willReturn(saleOrders);

        UserDTO userDTO = new UserDTO();
        userDTO.setLastName("1");
        userDTO.setFirstName("2");
        given(userClient.getUserByIdV1(userId)).willReturn(userDTO);
        given(redInvoiceRepository.save(any())).willReturn(new RedInvoice());

        CustomerDTO customer = new CustomerDTO();
        Response response = new Response();
        response.setData(customer);
        given(customerClient.getCustomerByIdV1(request.getCustomerId())).willReturn(response);

        RedInvoiceDTO result = serviceImp.create(request, userId, shopId);
        assertNotNull(result);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void searchProductTest() throws Exception {
        String uri = V1 + root + "/search-product";
        Long shopId = 1L;
        String keyWord = "123";
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        products.add(product);
        given(productRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(products);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            List<Price> prices = new ArrayList<>();
            Price price = new Price();
            price.setProductId(1L);
            price.setPriceNotVat(5.0);
            price.setVat(1.0);
            prices.add(price);
            given(productPriceRepo.findProductPrice(products.stream().map(item ->
                    item.getId()).collect(Collectors.toList()), 1, LocalDateTime.now())).willReturn(prices);

            List<ProductDataSearchDTO> result = productServiceImpl.findAllProduct(shopId, keyWord);
            assertNotNull(result);
        }

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void deleteTest() throws Exception {
        String uri = V1 + root + "/delete";
        Long id = 1L;

        ResponseMessage message =  ResponseMessage.DELETE_SUCCESSFUL;
        List<Long> ids = Arrays.asList(id);
        String saleOrderNumber = "123,456";
        given(redInvoiceRepository.getIdSaleOrder(id)).willReturn(saleOrderNumber);
        List<SaleOrder> saleOrders = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrders.add(saleOrder);

        given(saleOrderRepository.findSaleOrderByOrderCode(Arrays.asList(saleOrderNumber.split(",", -1)))).willReturn(saleOrders);
        given(redInvoiceDetailRepository.getAllRedInvoiceIds(id)).willReturn(new ArrayList<>());
        ResponseMessage result = serviceImp.deleteByIds(ids);
        assertNotNull(result);
        assertEquals(message.statusCode(), result.statusCode());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void updateTest() throws Exception {
        String uri = V1 + root + "/update";
        List<RedInvoiceRequest> redInvoiceRequests = new ArrayList<>();
        for(int i = 1; i < 2; i++){
            RedInvoiceRequest redInvoiceRequest = new RedInvoiceRequest();
            redInvoiceRequest.setId((long)i);
            redInvoiceRequest.setInvoiceNumber("NO_" + i);
            redInvoiceRequests.add(redInvoiceRequest);
        }
        ResponseMessage message =  ResponseMessage.CREATED;
        Long userId = 1L;
        Long shopId = 1L;
        RedInvoice redInvoice = new RedInvoice();
        redInvoice.setInvoiceNumber("NO_" + 1);
        given(redInvoiceRepository.findRedInvoiceByIdAndShopId(1L, shopId)).willReturn(redInvoice);

        ResponseMessage result = serviceImp.updateRed(redInvoiceRequests, userId, shopId);
        assertNotNull(result);
        assertEquals(message.statusCode(), result.statusCode());
        String inputJson = super.mapToJson(redInvoiceRequests);
        ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
