package vn.viettel.sale.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderComboDetail;
import vn.viettel.sale.entities.SaleOrderComboDiscount;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrderDiscount;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalOrderChoose;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleOrderComboDetailRepository;
import vn.viettel.sale.repository.SaleOrderComboDiscountRepository;
import vn.viettel.sale.repository.SaleOrderDetailRepository;
import vn.viettel.sale.repository.SaleOrderDiscountRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.PromotionClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.service.impl.OrderReturnImpl;

public class OrderReturnControllerTest extends BaseTest {
    private final String root = "/sales/order-return";

    @Mock
    private OrderReturnService service;

    @InjectMocks
    OrderReturnImpl serviceImp;

    @Mock
    SaleOrderRepository repository;

    @Mock
    CustomerClient customerClient;

    @Mock
    ApparamClient apparamClient;
    @Mock
    UserClient userClient;
    @Mock
    SaleOrderDetailRepository saleOrderDetailRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    ShopClient shopClient;

    @Mock
    PromotionClient promotionClient;
    @Mock
    SaleService saleService;
    @Mock
    SaleOrderDiscountRepository saleDiscount;
    @Mock
    SaleOrderComboDiscountRepository SaleComboDiscount;
    @Mock
    SaleOrderComboDetailRepository SaleComboDetail;
    @Mock
    StockTotalService stockTotalService;
    @Mock
    StockTotalRepository stockTotalRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final OrderReturnController controller = new OrderReturnController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void getAllOrderReturn() throws Exception{
        String uri = V1 + root;
        Long shopId = 1L;
        int type = 2;
        int size = 2;
        int page = 5;
        Sort newSort = Sort.by("orderNumber").descending();
        PageRequest pageRequest = PageRequest.of(page, size, newSort);
        SaleOrderFilter saleOrderFilter = new SaleOrderFilter();
        saleOrderFilter.setOrderNumber("123456");
        saleOrderFilter.setSearchKeyword("222");

        List<Long> cusIds = Arrays.asList(1L, 2L);
        List<Long> customerIds = Arrays.asList(1L, 2L);
        given(repository.getCustomerIds(saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(), type, shopId, null)).willReturn(cusIds);

        Response responseCusId = new Response<>();
        responseCusId.setData(customerIds);
        given(customerClient.getIdCustomerByV1(saleOrderFilter.getSearchKeyword(), saleOrderFilter.getCustomerPhone(), cusIds)).willReturn(responseCusId);

        List<SaleOrder> lstSaleOrder = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setId(1L);
        saleOrder.setCustomerId(1L);
        saleOrder.setFromSaleOrderId(1L);
        saleOrder.setFromSaleOrderId(1L);
        saleOrder.setCustomerId(1L);
        saleOrder.setTotalPromotionVat(2.0);
        saleOrder.setAmount(5.0);
        saleOrder.setTotal(5.0);
        lstSaleOrder.add(saleOrder);
        final Page<SaleOrder> findAll = new PageImpl<>(lstSaleOrder);
        given(repository.findALlSales(customerIds, saleOrderFilter.getFromDate(), saleOrderFilter.getToDate(), saleOrderFilter.getOrderNumber(),
                type, shopId, null, pageRequest)).willReturn(findAll);
        List<Long> saleCusIds = findAll.stream().map(item -> item.getCustomerId()).distinct().collect(Collectors.toList());

        List<SortDTO> customerSorts = new ArrayList<>();
        List<CustomerDTO> customers = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customers.add(customerDTO);
        given(customerClient.getCustomerInfoV1(customerSorts,null,saleCusIds)).willReturn(customers);

        given(repository.findAllById(findAll.getContent().stream().map(item -> item.getFromSaleOrderId()).
                collect(Collectors.toList()))).willReturn(lstSaleOrder);

        CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> data = serviceImp.getAllOrderReturn(saleOrderFilter, pageRequest, shopId);

        assertNotNull(data);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "2022/02/22")
                .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderReturnDetail() throws Exception {
        String uri = V1 + root + "/detail/{id}";
        Long orderReturnId = 1L;
        SaleOrder orderReturn = new SaleOrder();
        orderReturn.setFromSaleOrderId(1L);
        orderReturn.setCustomerId(1L);
        orderReturn.setReasonId("1L");
        orderReturn.setSalemanId(1L);
        given(repository.findById(orderReturnId)).willReturn(Optional.of(orderReturn));

        Response responseCus = new Response<>();
        CustomerDTO customer = new CustomerDTO();
        customer.setLastName("Phuong");
        customer.setFirstName("kt");
        responseCus.setData(customer);
        given(customerClient.getCustomerByIdV1(orderReturn.getCustomerId())).willReturn(responseCus);

        Response responseApp = new Response<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        responseApp.setData(apParamDTO);
        given(apparamClient.getApParamByCodeV1(orderReturn.getReasonId())).willReturn(responseApp);

        UserDTO user = new UserDTO();
        user.setLastName("A");
        user.setFirstName("B");
        given(userClient.getUserByIdV1(orderReturn.getSalemanId())).willReturn(user);

        List<SaleOrderDetail> productReturns = new ArrayList<>();
        SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
        saleOrderDetail.setProductId(1L);
        saleOrderDetail.setAutoPromotionVat(33.2);
        saleOrderDetail.setZmPromotionVat(22.0);
        saleOrderDetail.setQuantity(12);
        saleOrderDetail.setAmount(22.0);
        productReturns.add(saleOrderDetail);
        given(saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, false)).willReturn(productReturns);

        List<Product> products = new  ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        products.add(product);
        given(productRepository.getProducts(productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null)).willReturn(products);

        given(saleOrderDetailRepository.findSaleOrderDetail(orderReturnId, true)).willReturn(productReturns);

        OrderReturnDetailDTO data = serviceImp.getOrderReturnDetail(orderReturnId);
        assertNotNull(data);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void selectForReturn() throws Exception {
        String uri = V1 + root + "/choose";
        SaleOrderChosenFilter filter = new SaleOrderChosenFilter();
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
        filter.setSearchKeyword("SEARCH");
        filter.setOrderNumber("123456");
        filter.setProduct("1");
        Long shopId = 1L;

        Response response1 = new Response<>();
        response1.setData("3");
        given(shopClient.dayReturn(shopId)).willReturn(response1);

        Response response2 = new Response<>();
        List<Long> customerIds = Arrays.asList(-1L);
        response2.setData(customerIds);
        given(customerClient.getIdCustomerBySearchKeyWordsV1(filter.getSearchKeyword().trim())).willReturn(response2);

        List<SaleOrder> saleOrders = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setCustomerId(1L);
        saleOrder.setSalemanId(1L);
        saleOrder.setAmount(5.0);
        saleOrder.setTotal(2.0);
        saleOrders.add(saleOrder);
        List<UserDTO> users = new ArrayList<>();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        users.add(userDTO);

        List<CustomerDTO> customers = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customers.add(customerDTO);

        LocalDateTime newFromDate = DateUtils.convertFromDate(LocalDateTime.now().minusDays(3));
        LocalDateTime fromDate = DateUtils.convertFromDate(filter.getFromDate());
        LocalDateTime toDate = DateUtils.convertToDate(filter.getToDate());
        Mockito.when(repository.getSaleOrderForReturn(shopId, filter.getOrderNumber(), customerIds,
                filter.getProduct(), fromDate, toDate, newFromDate)).thenReturn(saleOrders);

        Mockito.when(userClient.getUserByIdsV1(saleOrders.stream().map(item -> item.getSalemanId()).
                distinct().filter(Objects::nonNull).collect(Collectors.toList()))).thenReturn(users);

        Mockito.when(customerClient.getCustomerInfoV1(new ArrayList<>(), 1, saleOrders.stream().map(item ->
                item.getCustomerId()).distinct().filter(Objects::nonNull).collect(Collectors.toList()))).thenReturn(customers);

        CoverResponse<List<SaleOrderDTO>, TotalOrderChoose> response = serviceImp.getSaleOrderForReturn(filter, shopId);
        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void orderSelected() throws Exception {
        String uri = V1 + root + "/chosen/{id}";

        Long id = 1L;
        Long shopId = 1L;
        SaleOrder orderReturn = new SaleOrder();
        orderReturn.setFromSaleOrderId(1L);
        orderReturn.setCustomerId(1L);
        orderReturn.setReasonId("1L");
        orderReturn.setSalemanId(1L);

        given(repository.findById(id)).willReturn(Optional.of(orderReturn));

        Response responseCus = new Response<>();
        CustomerDTO customer = new CustomerDTO();
        customer.setLastName("Phuong");
        customer.setFirstName("kt");
        responseCus.setData(customer);
        given(customerClient.getCustomerByIdV1(orderReturn.getCustomerId())).willReturn(responseCus);

        Response responseApp = new Response<>();
        ApParamDTO apParamDTO = new ApParamDTO();
        responseApp.setData(apParamDTO);
        given(apparamClient.getApParamByCodeV1(orderReturn.getReasonId())).willReturn(responseApp);

        UserDTO user = new UserDTO();
        user.setLastName("A");
        user.setFirstName("B");
        given(userClient.getUserByIdV1(orderReturn.getSalemanId())).willReturn(user);

        List<SaleOrderDetail> productReturns = new ArrayList<>();
        SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
        saleOrderDetail.setProductId(1L);
        saleOrderDetail.setAutoPromotionVat(33.2);
        saleOrderDetail.setZmPromotionVat(22.0);
        saleOrderDetail.setQuantity(12);
        saleOrderDetail.setAmount(22.0);
        productReturns.add(saleOrderDetail);
        given(saleOrderDetailRepository.findSaleOrderDetail(1L, false)).willReturn(productReturns);

        List<Product> products = new  ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        products.add(product);
        given(productRepository.getProducts(productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()), null)).willReturn(products);

        given(saleOrderDetailRepository.findSaleOrderDetail(1L, true)).willReturn(productReturns);

        Response response1 = new Response<>();
        List<ApParamDTO> apParamDTOList = new ArrayList<>();
        response1.setData(apParamDTOList);
        given(apparamClient.getApParamByTypeV1("SALEMT_MASTER_PAY_ITEM")).willReturn(response1);

        OrderReturnDetailDTO response = serviceImp.getSaleOrderChosen(id, shopId);
        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createOrderReturn() throws Exception {
        String uri = V1 + root;
        OrderReturnRequest request = new OrderReturnRequest();
        Long shopId = 1L;
        String userName = "phuongkt";
        request.setOrderNumber("SALE.UNITTEST");
//        requestObj.setDateReturn(LocalDateTime.of(2021,3,22,14,29,58));
        request.setReasonId("BREAKITEM");
        request.setReasonDescription("UnitTest");

        SaleOrder dtoObj = new SaleOrder();
        dtoObj.setId(1L);
        dtoObj.setOrderNumber("SALE.UNITTEST");
        dtoObj.setOrderDate(LocalDateTime.of(2021,3,22,14,29,58));
        dtoObj.setShopId(1L);
        dtoObj.setSalemanId(1L);
        dtoObj.setCustomerId(1L);
        dtoObj.setWareHouseTypeId(1L);
        dtoObj.setAmount(-50000D);
        dtoObj.setTotalPromotion(20000D);
        dtoObj.setTotal(-30000D);
        dtoObj.setTotalPaid(-30000D);
        dtoObj.setBalance(0D);
        dtoObj.setNote(request.getReasonDescription());
        dtoObj.setType(2);
        dtoObj.setFromSaleOrderId(1L);
        dtoObj.setAutoPromotionNotVat(0D);
        dtoObj.setAutoPromotionVat(0D);
        dtoObj.setAutoPromotion(0D);
        dtoObj.setZmPromotion(0D);
        dtoObj.setReasonId(request.getReasonId());
        dtoObj.setReasonDesc(request.getReasonDescription());
        dtoObj.setIsReturn(true);

        given(repository.getSaleOrderByNumber(request.getOrderNumber(), shopId)).willReturn(dtoObj);

        given(repository.checkIsReturn(dtoObj.getId())).willReturn(-1);

        List<SaleOrderDetail> productReturns = new ArrayList<>();
        SaleOrderDetail saleOrderDetail = new SaleOrderDetail();
        saleOrderDetail.setProductId(1L);
        saleOrderDetail.setAutoPromotionVat(33.2);
        saleOrderDetail.setZmPromotionVat(22.0);
        saleOrderDetail.setQuantity(12);
        saleOrderDetail.setAmount(22.0);
        saleOrderDetail.setTotal(23.0);
        saleOrderDetail.setAutoPromotionNotVat(22.0);
        saleOrderDetail.setAutoPromotionVat(22.0);
        saleOrderDetail.setZmPromotionNotVat(22.0);
        saleOrderDetail.setZmPromotionVat(22.0);
        saleOrderDetail.setAutoPromotion(22.0);
        saleOrderDetail.setZmPromotion(22.0);
        saleOrderDetail.setPromotionCode("12");
        productReturns.add(saleOrderDetail);
        given(saleOrderDetailRepository.findSaleOrderDetail(dtoObj.getId(), true)).willReturn(productReturns);
   //    given(saleOrderDetailRepository.findSaleOrderDetail(null, true)).willReturn(productReturns);

        Response response1 = new Response<>();
        ShopDTO shopDTO = new ShopDTO();
        response1.setData(shopDTO);
        given(shopClient.getByIdV1(shopId)).willReturn(response1);

        given(saleOrderDetailRepository.findSaleOrderDetail(dtoObj.getId(), false)).willReturn(productReturns);
//        given(saleOrderDetailRepository.findSaleOrderDetail(null, false)).willReturn(productReturns);

        List<SaleOrderDiscount> orderReturnDiscount = new ArrayList<>();
        SaleOrderDiscount discount = new SaleOrderDiscount();
        discount.setPromotionCode("11");
        discount.setDiscountAmountVat(2.0);
        discount.setDiscountAmountNotVat(6.0);
        discount.setDiscountAmount(6.0);
        discount.setMaxDiscountAmount(6.0);
        discount.setPromotionType("ZV23");
        discount.setPromotionProgramId(1L);
        orderReturnDiscount.add(discount);
        given(saleDiscount.findAllBySaleOrderId(dtoObj.getId())).willReturn(orderReturnDiscount);

        Response response2 = new Response<>();
        PromotionProgramDTO programDTO = new PromotionProgramDTO();
        programDTO.setDiscountPriceType(1);
        programDTO.setDiscountPriceType(0);
        response2.setData(programDTO);
        given(promotionClient.getByCode(discount.getPromotionCode())).willReturn(response2);

        List<SaleOrderComboDiscount> comboDiscounts = new ArrayList<>();
        SaleOrderComboDiscount comboDiscount = new SaleOrderComboDiscount();
        comboDiscount.setDiscountAmount(Float.parseFloat("6"));
        comboDiscount.setDiscountAmountNotVat(Float.parseFloat("6"));
        comboDiscount.setDiscountAmountVat(Float.parseFloat("6"));
        comboDiscounts.add(comboDiscount);
        given(SaleComboDiscount.findAllBySaleOrderId(dtoObj.getId())).willReturn(comboDiscounts);

        List<SaleOrderComboDetail> comboDetails = new ArrayList<>();
        SaleOrderComboDetail comboDetail = new SaleOrderComboDetail();
        comboDetail.setComboQuantity(2);
        comboDetail.setQuantity(2);
        comboDetail.setPriceNotVat(6.0);
        comboDetail.setAmount(6.0);
        comboDetail.setTotal(6.0);
        comboDetail.setAutoPromotion(6.0);
        comboDetail.setAutoPromotionVat(6.0);
        comboDetail.setAutoPromotionNotVat(6.0);
        comboDetail.setZmPromotion(6.0);
        comboDetail.setZmPromotionVat(6.0);
        comboDetail.setZmPromotionNotVat(6.0);
        comboDetails.add(comboDetail);
        given(SaleComboDetail.findAllBySaleOrderId(dtoObj.getId())).willReturn(comboDetails);

        List<StockTotal> stockTotals1 = new ArrayList<>();
        StockTotal stockTotal = new StockTotal();
        stockTotals1.add(stockTotal);
        given(stockTotalRepository.getStockTotal(shopId, 1L,
                productReturns.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList()))).willReturn(stockTotals1);

        Response responseCus = new Response<>();
        CustomerDTO customer = new CustomerDTO();
        customer.setLastName("Phuong");
        customer.setFirstName("kt");
        responseCus.setData(customer);
        given(customerClient.getCustomerByIdV1(dtoObj.getCustomerId())).willReturn(responseCus);

        Response responsePro = new Response<>();
        List<RPT_ZV23DTO> rpt_zv23DTOS = new ArrayList<>();
        RPT_ZV23DTO rpt_zv23DTO = new RPT_ZV23DTO();
        rpt_zv23DTO.setPromotionProgramId(1L);
        rpt_zv23DTO.setId(1L);
        rpt_zv23DTOS.add(rpt_zv23DTO);
        responsePro.setData(rpt_zv23DTOS);

        List<SaleOrderDiscount> discountZV23S = orderReturnDiscount.stream().filter(d -> d.getPromotionType().equalsIgnoreCase("ZV23")).collect(Collectors.toList());

        Map<Long, List<SaleOrderDiscount>> zv23MapProducts = discountZV23S.stream().collect(
                Collectors.groupingBy(SaleOrderDiscount::getPromotionProgramId, LinkedHashMap::new, Collectors.toList()));

        given(promotionClient.findByProgramIdsV1(zv23MapProducts.keySet(), customer.getId())).willReturn(responsePro);
        
        Mockito.when(saleService.safeSave(Mockito.isA(SaleOrder.class), Mockito.isA(ShopDTO.class))).thenReturn(dtoObj);
        List<Long> lst = Arrays.asList(22l);
        
        Response response3 = new Response<>();
        response3.setData(lst);
        given(promotionClient.returnPromotionShopmap(Mockito.isA(Map.class))).willReturn(response3);
        HashMap<String, Object> response = serviceImp.createOrderReturn(request, shopId, userName);
//        assertNotNull(response);

        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}