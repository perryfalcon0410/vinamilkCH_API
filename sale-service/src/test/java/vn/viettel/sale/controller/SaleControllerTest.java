package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.*;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.service.impl.SaleOrderServiceImpl;
import vn.viettel.sale.service.impl.SaleServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SaleControllerTest extends BaseTest {

    private final String root = "/sales/sale-orders";

    @InjectMocks
    SaleOrderServiceImpl saleOrderService;

    @InjectMocks
    SaleServiceImpl serviceImp;

    @Mock
    SaleService service;

    @Mock
    ProductService productService;

    @Mock
    SalePromotionService salePromotionService;

    @Mock
    SaleOrderRepository repository;

    @Mock
    CustomerClient customerClient;

    @Mock
    UserClient userClient;

    @Mock
    ApparamClient apparamClient;

    @Mock
    SaleOrderDetailRepository saleOrderDetailRepository;

    @Mock
    SaleOrderDiscountRepository saleOrderDiscountRepository;

    @Mock
    ShopClient shopClient;

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
    OnlineOrderRepository onlineOrderRepo;
    @Mock
    OnlineOrderDetailRepository onlineOrderDetailRepo;
    @Mock
    CustomerTypeClient customerTypeClient;
    @Mock
    PromotionClient promotionClient;
    @Mock
    OnlineOrderService onlineOrderService;
    @Mock
    SalePromotionService salePromotionService;
    @Mock
    SaleOrderDiscountRepository saleOrderDiscountRepo;
    @Mock
    SaleOrderComboDiscountRepository saleOrderComboDiscountRepo;
    @Mock
    StockTotalService stockTotalService;

    private List<SaleOrder> lstEntities;

    private List<ApParamDTO> apParamDTOS;

    private List<CustomerDTO> customerDTOS;

    private List<UserDTO> userDTOS;

    private List<SaleOrderDiscount> saleOrderDiscounts;
    ShopDTO shop;
    CustomerTypeDTO customerType;
    SaleOrderRequest request;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        saleOrderService.setModelMapper(this.modelMapper);
        final SaleController controller = new SaleController();
        controller.setService(service);
        this.setupAction(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.setId(i);
            saleOrder.setCustomerId(1L);
            saleOrder.setSalemanId(1L);
            saleOrder.setOrderType(1);
            saleOrder.setMemberCardAmount(1000D);
            saleOrder.setTotalPromotionVat(100D);
            saleOrder.setSalemanId(1L);
            saleOrder.setDeliveryType(1);
            lstEntities.add(saleOrder);
        }

        apParamDTOS = new ArrayList<>();
        final ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setId(1L);
        apParamDTO.setValue("1");
        apParamDTOS.add(apParamDTO);

        customerDTOS = new ArrayList<>();
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setLastName("A");
        customerDTO.setFirstName("A");
        customerDTOS.add(customerDTO);

        userDTOS = new ArrayList<>();
        final UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTOS.add(userDTO);

        saleOrderDiscounts = new ArrayList<>();
        final SaleOrderDiscount saleOrderDiscount = new SaleOrderDiscount();
        saleOrderDiscount.setId(1L);
        saleOrderDiscounts.add(saleOrderDiscount);

        request = new SaleOrderRequest();
        SalePromotionDTO salePromotionDTO = new SalePromotionDTO();
        List<SalePromotionDTO> promotionDTOS = Arrays.asList(salePromotionDTO);
        request.setPromotionInfo(promotionDTOS);
        ProductOrderRequest dto = new ProductOrderRequest();
        dto.setQuantity(5);
        List<ProductOrderRequest> products = Arrays.asList(dto);
        request.setProducts(products);

        Mockito.when(customerClient.getCustomerByIdV1(1L)).thenReturn(new Response<CustomerDTO>().withData(customerDTOS.get(0)));

        shop = new ShopDTO();
        shop.setId(1L);

        Mockito.when(shopClient.getByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shop));

        Mockito.when(apparamClient.getApParamByTypeAndvalue(null, "1"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        customerType = new CustomerTypeDTO();
        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L)).thenReturn(customerType);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(saleOrderDiscountRepository.findAllBySaleOrderId(id))
                .thenReturn(saleOrderDiscounts);
    }

    @Test
    public void getAllSaleOrder() throws Exception {
        String uri = V1 + root;
        int size = 1;
        int page = 5;
        SaleOrderFilter saleOrderFilter = new SaleOrderFilter();

        Mockito.when(repository.findALlSales(null,null,null,null,1,1L,null,PageRequest.of(page,size)))
                .thenReturn(new PageImpl<>(lstEntities));

        Mockito.when(apparamClient.getApParams(null, lstEntities.stream().map(item -> {
                    if(item.getOrderType()!=null) {
                        return  item.getOrderType().toString();
                    }else {
                        return null;
                    }
                }).distinct().filter(Objects::nonNull).collect(Collectors.toList())))
                .thenReturn(new Response<List<ApParamDTO>>().withData(apParamDTOS));

        CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> allSaleOrder
                = saleOrderService.getAllSaleOrder(saleOrderFilter, PageRequest.of(page, size), 1L);

        assertEquals(lstEntities.size(), allSaleOrder.getResponse().getTotalElements());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

//        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getSaleOrderDetail() throws Exception {
        String uri = V1 + root + "?saleOrderId=1&orderNumber=OD12";

        Mockito.when(repository.findById(1L)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(customerClient.getCustomerByIdV1(1L)).thenReturn(new Response<CustomerDTO>().withData(customerDTOS.get(0)));

        Mockito.when(userClient.getUserByIdV1(lstEntities.get(0).getSalemanId())).thenReturn(userDTOS.get(0));

        SaleOrderDetailDTO saleOrderDetail = saleOrderService.getSaleOrderDetail(1L, "1");

        assertNotNull(saleOrderDetail);

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void printSaleOrder() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/print-sale-order/" + id.toString();

        PrintSaleOrderDTO dto = saleOrderService.printSaleOrder(id, 1L);
        assertNotNull(dto);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createSaleOrder() throws Exception {

        Long id = 1L;
        String uri = V1 + root;



        serviceImp.createSaleOrder(request, 1L, 1L, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions() {
    }

    @Test
    public void getPromotionProduct() {
    }

    @Test
    public void promotionCalculation() {
    }

    @Test
    public void getPriceByPrID() {
    }

    @Test
    public void printTempSaleOrder() {
    }

    @Test
    public void getDiscountCode() {
    }

    @Test
    public void getClientIp() {
    }
}
