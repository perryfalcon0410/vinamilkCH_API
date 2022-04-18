package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.OnlineOrderServiceImpl;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OnlineOrderControllerTest extends BaseTest {
    private final String root = "/sales/online-orders";

    @InjectMocks
    OnlineOrderServiceImpl serviceImp;

    @Mock
    OnlineOrderService service;

    @Mock
    OnlineOrderRepository repository;

    @Mock
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    CustomerClient customerClient;

    @Mock
    ProductRepository productRepo;

    @Mock
    ProductPriceRepository productPriceRepo;

    @Mock
    StockTotalRepository stockTotalRepo;

    @Mock
    ShopClient shopClient;

    @Mock
    private CustomerOnlRequest cusRequest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final OnlineOrderController controller = new OnlineOrderController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void findOnlineOrdersSuccessTest() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;

        Sort newSort = Sort.by("orderNumber").descending();
        PageRequest pageRequest = PageRequest.of(page, size, newSort);
        List<OnlineOrder> list = Arrays.asList(new OnlineOrder(), new OnlineOrder());
        OnlineOrderFilter filter = new OnlineOrderFilter();
        final Page<OnlineOrder> pageData = new PageImpl<>(list);
        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            Mockito.when(repository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(pageData);
        }
        Page<OnlineOrderDTO> onlineOrderDTOS = serviceImp.getOnlineOrders(filter, pageRequest);

        assertNotNull(onlineOrderDTOS);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOnlineOrderSuccessTest() throws Exception {
        String uri = V1 + root + "/{id}";
        OnlineOrder onlineOrder = new OnlineOrder();
        onlineOrder.setId(1L);
        onlineOrder.setOrderNumber("123");
        onlineOrder.setCustomerName("CustomerName");
        onlineOrder.setCustomerPhone("123456");
        Long id = 1L;
        Long shopId = 1L;
        Mockito.when(repository.getById(id, shopId)).thenReturn(java.util.Optional.of(onlineOrder));

        List<OnlineOrderDetail> orderDetails = new ArrayList<>();
        OnlineOrderDetail detail = new OnlineOrderDetail();
        detail.setSku("1");
        orderDetails.add(detail);
        Mockito.when(onlineOrderDetailRepo.findByOnlineOrderId(id)).thenReturn(orderDetails);
        List<CustomerDTO> lstCustomerDTO = new ArrayList<>();
        Response response = new Response<>();
        response.setData(lstCustomerDTO);
        Mockito.when(customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone())).thenReturn(response);

        CustomerOnlRequest classToBeTestedSpy = Mockito.spy(new CustomerOnlRequest());
        Mockito.doReturn(cusRequest).when(classToBeTestedSpy);
        Response response1 = new Response<>();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerTypeId(1L);
        response1.setData(customerDTO);
        cusRequest.setStatus(1);
        cusRequest.setMobiPhone(onlineOrder.getCustomerPhone());
        cusRequest.setDob(onlineOrder.getCustomerDOB());
        cusRequest.setAddress(onlineOrder.getCustomerAddress());
        cusRequest.setLastName("KH");
        cusRequest.setFirstName("CustomerName");

        Mockito.when(customerClient.createForFeignV1(cusRequest, shopId)).thenReturn(response1);

        CustomerTypeDTO customerType = new CustomerTypeDTO();
        Mockito.when(customerTypeClient.getCusTypeById(customerDTO.getCustomerTypeId())).thenReturn(customerType);

        List<Product> products = new ArrayList<>();
        Product product =  new Product();
        product.setId(1L);
        product.setProductCode("code");
        products.add(product);
        Mockito.when(productRepo.findByProductCodes(orderDetails.stream().map(item -> item.getSku()).distinct().
                filter(Objects::nonNull).collect(Collectors.toList()))).thenReturn(products);
        List<Long> productIds = products.stream().map(item -> item.getId()).collect(Collectors.toList());
        List<Price> prices = new ArrayList<>();
        Price price =  new Price();
        price.setId(1L);
        prices.add(price);
        List<StockTotal> stockTotals = new ArrayList<>();
        StockTotal stockTotal =  new StockTotal();
        stockTotal.setId(1L);
        stockTotals.add(stockTotal);

        try(MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String instantExpected = "2022-02-22T10:15:30Z";
            Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
            LocalDateTime now = LocalDateTime.now(clock);
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            Mockito.when(productPriceRepo.findProductPriceWithType(productIds, customerType.getId(),
                    DateUtils.convertToDate(LocalDateTime.now()))).thenReturn(prices);

            Mockito.when(stockTotalRepo.getStockTotal(shopId, customerType.getWareHouseTypeId(), productIds)).thenReturn(stockTotals);

            Response response2 = new Response<>();
            response2.setData(true);
            Mockito.when(shopClient.isEditableOnlineOrderV1(shopId)).thenReturn(response2);

            OnlineOrderDTO dto = serviceImp.getOnlineOrder(id, shopId, 1L);

            assertNotNull(dto);
        }

        ResultActions resultActions = mockMvc.perform(get(uri, "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
