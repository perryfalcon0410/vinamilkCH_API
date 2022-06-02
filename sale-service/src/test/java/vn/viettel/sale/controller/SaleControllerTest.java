package vn.viettel.sale.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.*;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.service.impl.DefinedObject;
import vn.viettel.sale.service.impl.SaleOrderServiceImpl;
import vn.viettel.sale.service.impl.SalePromotionServiceImpl;
import vn.viettel.sale.service.impl.SaleServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class SaleControllerTest extends BaseTest {

    private final String root = "/sales";

    @Mock
    SaleOrderServiceImpl saleOrderService;

    @InjectMocks
    SaleServiceImpl serviceImp;
    @InjectMocks
    SalePromotionServiceImpl salePromotionServiceImpl;

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
    SaleOrderDiscountRepository saleOrderDiscountRepo;
    @Mock
    SaleOrderComboDiscountRepository saleOrderComboDiscountRepo;
    @Mock
    StockTotalService stockTotalService;
    @Mock
    DefinedObject definedObject;

    @Mock
    public EntityManager entityManager;
    @Mock
    MemberCardClient memberCardClient;
    private List<SaleOrder> lstEntities;

    private List<ApParamDTO> apParamDTOS;

    private List<CustomerDTO> customerDTOS;

    private List<UserDTO> userDTOS;

    private List<SaleOrderDiscount> saleOrderDiscounts;
    ShopDTO shop;
    CustomerTypeDTO customerType;
    SaleOrderRequest request;
    long shopId = 1L;
    MemberCardDTO memberCard;

    OrderPromotionRequest orderRequest;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        salePromotionServiceImpl.setModelMapper(this.modelMapper);
        final SaleController controller = new SaleController();
        controller.setService(service);
        controller.setSalePromotionService(salePromotionService);
        this.setupAction(controller);

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
        apParamDTO.setApParamCode("ONLINE");
        apParamDTOS.add(apParamDTO);

        customerDTOS = new ArrayList<>();
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setLastName("A");
        customerDTO.setFirstName("A");
        customerDTO.setCustomerTypeId(1L);
        customerDTO.setCloselyTypeId(1L);
        customerDTO.setCardTypeId(1L);
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
        request.setCustomerId(1L);
        request.setOrderType(1);

        FreeProductDTO freeProductDTO = new FreeProductDTO();
        freeProductDTO.setStockQuantity(500);
        freeProductDTO.setProductId(1L);
        freeProductDTO.setQuantity(100);
        List<FreeProductDTO> freeProductDTOs = Arrays.asList(freeProductDTO);

        SalePromotionDiscountDTO amount = new SalePromotionDiscountDTO();
        amount.setAmount(456.6);
        SalePromotionDTO inputPro = new SalePromotionDTO();
        inputPro.setProgramId(1L);
        inputPro.setIsUse(true);
        inputPro.setProgramType("ZM");
        inputPro.setProducts(freeProductDTOs);
        inputPro.setContraintType(1);
        inputPro.setTotalQty(100);
        inputPro.setAmount(amount);
        SalePromotionDTO inputPro1 = new SalePromotionDTO();
        inputPro1.setProgramId(1L);
        inputPro1.setIsUse(true);
        inputPro1.setProgramType("ZV");
//        inputPro1.setProducts(freeProductDTOs);
        inputPro1.setContraintType(1);
        inputPro1.setTotalQty(100);
        inputPro1.setAmount(amount);

        List<SalePromotionDTO> promotionDTOS = new ArrayList<>();
        promotionDTOS.add(inputPro);
        promotionDTOS.add(inputPro1);
        request.setPromotionInfo(promotionDTOS);
        ProductOrderRequest dto = new ProductOrderRequest();
        dto.setQuantity(5);
        dto.setProductId(1L);
        dto.setProductCode("14563");

        List<ProductOrderRequest> products = new ArrayList<>();
        products.add(dto);
        request.setProducts(products);

        Mockito.when(customerClient.getCustomerByIdV1(1L)).thenReturn(new Response<CustomerDTO>().withData(customerDTO));
        Mockito.when(customerClient.getCustomerByIdV1(0L)).thenReturn(new Response<CustomerDTO>().withData(customerDTO));
        shop = new ShopDTO();
        shop.setId(1L);

        Mockito.when(shopClient.getByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shop));

        Mockito.when(apparamClient.getApParamByTypeAndvalue(null, request.getOrderType().toString()))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        customerType = new CustomerTypeDTO();
        customerType.setWareHouseTypeId(1L);
        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L)).thenReturn(customerType);

//        Mockito.when(repository.findById(1L)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

//        Mockito.when(saleOrderDiscountRepository.findAllBySaleOrderId(1L))
//                .thenReturn(saleOrderDiscounts);

        List<Long> productNotAccumulated = Arrays.asList(1L);
        Response responseAccumulated = new Response();
        responseAccumulated.setData(productNotAccumulated);
        Mockito.when(promotionClient.getProductsNotAccumulatedV1(Arrays.asList(dto.getProductId()))).thenReturn(responseAccumulated);

        Price price = new Price();
        price.setProductId(1L);
        price.setPrice(8000.0);
        price.setPriceNotVat(7000.0);
        List<Price> productPrices = Arrays.asList(price);
        Mockito.when(priceRepository.findProductPriceWithType(Arrays.asList(dto.getProductId()),
                customerDTO.getCustomerTypeId(), DateUtils.convertToDate(LocalDateTime.now()))).thenReturn(productPrices);

        Mockito.when(productRepository.findProductWithStock(shopId, customerType.getWareHouseTypeId(),
                Arrays.asList(1L))).thenReturn(freeProductDTOs);

        OrderVoucherRequest orderVoucher = new OrderVoucherRequest();
        orderVoucher.setId(1L);
        orderVoucher.setPrice(555.5);
        List<OrderVoucherRequest> vouchers = Arrays.asList(orderVoucher);
        request.setVouchers(vouchers);
        request.setTotalOrderAmount(50000.0);
        request.setDiscountCode("123");
        request.setDiscountAmount(457.0);
        request.setOnlineNumber("1456");
        request.setOrderOnlineId(1L);
        VoucherDTO voucher = new VoucherDTO();
        voucher.setPrice(555.5);
        voucher.setIsUsed(false);
        Response responseVoucherDTO = new Response();
        responseVoucherDTO.setData(voucher);
        Mockito.when(promotionClient.getVouchersV1(orderVoucher.getId())).thenReturn(responseVoucherDTO);

        SalePromotionCalculationDTO calculationDTO = new SalePromotionCalculationDTO();
        calculationDTO.setLstSalePromotions(promotionDTOS);
        HashMap<Long,Double> mapMoneys = new HashMap<>();
        mapMoneys.put(inputPro.getProgramId(), inputPro.getAmount().getAmount());
        mapMoneys.put(inputPro1.getProgramId(), inputPro1.getAmount().getAmount());
        OrderPromotionRequest orderRequest = Mockito.mock(OrderPromotionRequest.class);
        orderRequest.setCustomerId(request.getCustomerId());
        orderRequest.setOrderType(request.getOrderType());
        orderRequest.setProducts(Arrays.asList(dto));
        Mockito.when(definedObject.newOrderPromotionRequest()).thenReturn(orderRequest);
        Mockito.when(salePromotionService.getSaleItemPromotions(orderRequest, shopId, mapMoneys, true)).thenReturn(calculationDTO);
        Mockito.when(salePromotionService.checkPromotionLimit(inputPro, shopId)).thenReturn(true);
//        Mockito.when(salePromotionService.checkPromotionLimit(inputPro1, shopId)).thenReturn(true);
        Response responsePromotionShopMapDTO = new Response();
        PromotionShopMapDTO promotionShopMapDTO = new PromotionShopMapDTO();
        responsePromotionShopMapDTO.setData(promotionShopMapDTO);
        Mockito.when(promotionClient.getPromotionShopMapV1(inputPro.getProgramId(), shopId)).thenReturn(responsePromotionShopMapDTO);
        this.orderRequest = orderRequest;
        orderRequest.setPromotionAmount(0.0);
        orderRequest.setPromotionAmountExTax(0.0);
        Mockito.when(salePromotionService.getDiscountCode(request.getDiscountCode(), shopId, orderRequest )).thenReturn(inputPro);
        List<ComboProductDetailDTO> combos = new ArrayList<>();
        ComboProductDetailDTO comboProductDetailDTO = new ComboProductDetailDTO();
        combos.add(comboProductDetailDTO);
//        Mockito.when(comboProductRepository.findComboProduct(Arrays.asList(1L, 1L, 1L))).thenReturn(combos);

        Response responsePromotion = new Response();
        PromotionProgramDiscountDTO discountNeedSave = new PromotionProgramDiscountDTO();
        PromotionProgramDTO program = new PromotionProgramDTO();
        program.setType("ZM");
        program.setPromotionDateTime(20);
        discountNeedSave.setProgram(program);
        responsePromotion.setData(discountNeedSave);
        Mockito.when(promotionClient.getPromotionDiscount(request.getDiscountCode(), shopId)).thenReturn(responsePromotion);

        Response responseshopClient = new Response();
        responseshopClient.setData(true);
//        Mockito.when(shopClient.isManuallyCreatableOnlineOrderV1(shopId)).thenReturn(responseshopClient);

        OnlineOrder onlineOrder = new OnlineOrder();
        Mockito.when(onlineOrderRepo.getById(request.getOrderOnlineId(), shopId)).thenReturn(Optional.of(onlineOrder));

        List<OnlineOrderDetail> onlineDetails = new ArrayList<>();
        OnlineOrderDetail onlineOrderDetail = new OnlineOrderDetail();
        onlineOrderDetail.setSku("14563");
        onlineOrderDetail.setQuantity(5);
        onlineDetails.add(onlineOrderDetail);
        Mockito.when(onlineOrderDetailRepo.findByOnlineOrderId(request.getOrderOnlineId())).thenReturn(onlineDetails);

        Response responseOnline = new Response();
        responseOnline.setData(false);
        Mockito.when(shopClient.isEditableOnlineOrderV1(shopId)).thenReturn(responseOnline);

        EntityManagerFactory entityManagerFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(entityManager.getEntityManagerFactory()).thenReturn(entityManagerFactory);

        EntityManager newEntityM = Mockito.mock(EntityManager.class);
        Mockito.when(entityManagerFactory.createEntityManager()).thenReturn(newEntityM);

        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Mockito.when(newEntityM.getTransaction()).thenReturn(transaction);

        LocalDateTime now = DateUtils.convertDateToLocalDateTime(new Date());
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();

        String  year = Integer.toString(now.getYear()).substring(2);
        String code = "SAL." +  shop.getShopCode() + year + Integer.toString(month + 100).substring(1)  + Integer.toString(day + 100).substring(1);
        LocalDateTime start =  DateUtils.convertFromDate(now);
        List<SaleOrder> lstSaleOrder = new ArrayList<>();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setOrderNumber("12345");
        lstSaleOrder.add(saleOrder);
        Page<SaleOrder> saleOrders = new PageImpl<>(lstSaleOrder, PageRequest.of(0,1), lstSaleOrder.size());
        Mockito.when(repository.getLastSaleOrderNumber(1L, code, start, PageRequest.of(0,1))).thenReturn(saleOrders);

        discountNeedSave.setPromotionProgramId(1L);
        Mockito.when(promotionClient.getPromotionShopMapV1(discountNeedSave.getPromotionProgramId(), shopId)).thenReturn(responsePromotionShopMapDTO);

        Mockito.when(promotionClient.updatePromotionShopMapV1(promotionShopMapDTO)).thenReturn(responsePromotionShopMapDTO);

        orderPromotionRequest.setCustomerId(1L);
        orderPromotionRequest.setOrderType(1);
        orderPromotionRequest.setProducts(products);

        memberCard = new MemberCardDTO();
        memberCard.setId(1L);
        Response responseMemberCardDTO = new Response();
        responseMemberCardDTO.setData(memberCard);
        Mockito.when(memberCardClient.getByCustomerId(1L)).thenReturn(responseMemberCardDTO);

        Mockito.when(promotionClient.checkSaleProductByProgramIdV1(1L, Arrays.asList(1L))).thenReturn(responseshopClient);

        List<PromotionProductOpenDTO> freeProducts = new ArrayList<>();
        PromotionProductOpenDTO promotionProductOpenDTO = new PromotionProductOpenDTO();
        promotionProductOpenDTO.setProductId(1L);
        freeProducts.add(promotionProductOpenDTO);
        Response responsePromotionProductOpenDTO = new Response();
        responsePromotionProductOpenDTO.setData(freeProducts);
        Mockito.when(promotionClient.getFreeItemV1(1L)).thenReturn(responsePromotionProductOpenDTO);

        Mockito.when(productRepository.findFreeProductDTONoOrders(shopId, 1L, Arrays.asList(1L))).thenReturn(freeProductDTOs);

        List<PromotionProgramDetailDTO> details = new ArrayList<>();
        PromotionProgramDetailDTO promotionProgramDetailDTO = new PromotionProgramDetailDTO();
        promotionProgramDetailDTO.setSaleUom("1");
        promotionProgramDetailDTO.setProductId(1L);
        promotionProgramDetailDTO.setFreeProductId(1L);
        promotionProgramDetailDTO.setFreeUom("1");
        promotionProgramDetailDTO.setOrderNumber(2);
        promotionProgramDetailDTO.setDiscAmt(200.0);
        promotionProgramDetailDTO.setFreeQty(1);
        promotionProgramDetailDTO.setDisPer(2.0);
        promotionProgramDetailDTO.setSaleAmt(50000.0);
        details.add(promotionProgramDetailDTO);
        Response responsePromotionProgramDetailDTO = new Response();
        responsePromotionProgramDetailDTO.setData(details);
        Mockito.when(promotionClient.findPromotionProgramDetailV1(1L, Arrays.asList(1L))).thenReturn(responsePromotionProgramDetailDTO);
        Mockito.when(promotionClient.findPromotionProgramDetailV1(1L, null)).thenReturn(responsePromotionProgramDetailDTO);

        List<PromotionProgramDiscountDTO> programDiscounts = new ArrayList<>();
        PromotionProgramDiscountDTO promotionProgramDiscountDTO = new PromotionProgramDiscountDTO();
        promotionProgramDiscountDTO.setDiscountPercent(2.1);
        promotionProgramDiscountDTO.setMinSaleAmount(1.0);
        promotionProgramDiscountDTO.setDiscountPercent(2.0);
        promotionProgramDiscountDTO.setMaxSaleAmount(3000.0);
        promotionProgramDiscountDTO.setMaxDiscountAmount(6000.0);
        programDiscounts.add(promotionProgramDiscountDTO);
        PromotionProgramDiscountDTO promotionProgramDiscountDTO1 = new PromotionProgramDiscountDTO();
        promotionProgramDiscountDTO1.setDiscountAmount(5000.3);
        promotionProgramDiscountDTO1.setMinSaleAmount(1.0);
        promotionProgramDiscountDTO1.setDiscountAmount(2000.1);
//        promotionProgramDiscountDTO1.setGivenType(3);
        programDiscounts.add(promotionProgramDiscountDTO1);
        Response responsePromotionProgramDiscountDTO = new Response();
        responsePromotionProgramDiscountDTO.setData(programDiscounts);
        Mockito.when(promotionClient.findPromotionDiscountByPromotion(1L)).thenReturn(responsePromotionProgramDiscountDTO);

        ShopParamDTO shopParamDTO = new ShopParamDTO();
        Response responseShopParamDTO = new Response();
        responseShopParamDTO.setData(shopParamDTO);
        Mockito.when(shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", shopId)).thenReturn(responseShopParamDTO);

        RPT_ZV23DTO rpt_zv23DTO = new RPT_ZV23DTO();
//        rpt_zv23DTO.setTotalAmount(5000.0);
        Response responseRPT_ZV23DTO = new Response();
        responseRPT_ZV23DTO.setData(rpt_zv23DTO);
        Mockito.when(promotionClient.checkZV23RequireV1("123",1L,shopId)).thenReturn(responseRPT_ZV23DTO);


    }

    @Test
    public void printTempSaleOrder() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/printtmp";

        serviceImp.createSaleOrder(request, 1L, 1L, true);

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

        serviceImp.createSaleOrder(request, 1L, shopId, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    OrderPromotionRequest orderPromotionRequest = new OrderPromotionRequest();
    @Test
    public void getOrderPromotions_ZV01() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO =  new PromotionProgramDTO();
        promotionProgramDTO.setId(1L);
        promotionProgramDTO.setType("ZV01");
        promotionProgramDTO.setGivenType(1);
        programs.add(promotionProgramDTO);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV02() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO02 =  new PromotionProgramDTO();
        promotionProgramDTO02.setId(1L);
        promotionProgramDTO02.setType("ZV02");
        promotionProgramDTO02.setGivenType(1);
        programs.add(promotionProgramDTO02);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV03() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO03 =  new PromotionProgramDTO();
        promotionProgramDTO03.setId(1L);
        promotionProgramDTO03.setType("ZV03");
        promotionProgramDTO03.setGivenType(1);
        programs.add(promotionProgramDTO03);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV07() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO5 =  new PromotionProgramDTO();
        promotionProgramDTO5.setId(1L);
        promotionProgramDTO5.setType("ZV07");
        promotionProgramDTO5.setGivenType(3);
        programs.add(promotionProgramDTO5);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV08() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO5 =  new PromotionProgramDTO();
        promotionProgramDTO5.setId(1L);
        promotionProgramDTO5.setType("ZV08");
        promotionProgramDTO5.setGivenType(3);
        programs.add(promotionProgramDTO5);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV09() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO5 =  new PromotionProgramDTO();
        promotionProgramDTO5.setId(1L);
        promotionProgramDTO5.setType("ZV09");
        promotionProgramDTO5.setGivenType(3);
        programs.add(promotionProgramDTO5);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV13() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO5 =  new PromotionProgramDTO();
        promotionProgramDTO5.setId(1L);
        promotionProgramDTO5.setType("ZV13");
        promotionProgramDTO5.setGivenType(3);
        programs.add(promotionProgramDTO5);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV14() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO6 =  new PromotionProgramDTO();
        promotionProgramDTO6.setId(1L);
        promotionProgramDTO6.setType("ZV14");
        promotionProgramDTO6.setGivenType(1);
        programs.add(promotionProgramDTO6);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV15() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO6 =  new PromotionProgramDTO();
        promotionProgramDTO6.setId(1L);
        promotionProgramDTO6.setType("ZV15");
        promotionProgramDTO6.setGivenType(1);
        programs.add(promotionProgramDTO6);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV19() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO1 =  new PromotionProgramDTO();
        promotionProgramDTO1.setId(1L);
        promotionProgramDTO1.setType("ZV19");
        promotionProgramDTO1.setGivenType(1);
        programs.add(promotionProgramDTO1);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV20() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO8 =  new PromotionProgramDTO();
        promotionProgramDTO8.setId(1L);
        promotionProgramDTO8.setType("ZV20");
        promotionProgramDTO8.setGivenType(3);
        programs.add(promotionProgramDTO8);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV21() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO8 =  new PromotionProgramDTO();
        promotionProgramDTO8.setId(1L);
        promotionProgramDTO8.setType("ZV21");
        promotionProgramDTO8.setGivenType(3);
        programs.add(promotionProgramDTO8);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV23() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO2 =  new PromotionProgramDTO();
        promotionProgramDTO2.setId(1L);
        promotionProgramDTO2.setType("ZV23");
        promotionProgramDTO2.setGivenType(1);
        promotionProgramDTO2.setPromotionProgramCode("123");
        programs.add(promotionProgramDTO2);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        List<PromotionProgramProductDTO> programProduct = new ArrayList<>();
        PromotionProgramProductDTO promotionProgramProductDTO = new PromotionProgramProductDTO();
        promotionProgramProductDTO.setType(1);
        promotionProgramProductDTO.setProductId(1L);
        programProduct.add(promotionProgramProductDTO);
        Response responsePromotionProgramProductDTO = new Response();
        responsePromotionProgramProductDTO.setData(programProduct);
        Mockito.when(promotionClient.findByPromotionIdsV1(Arrays.asList(1L))).thenReturn(responsePromotionProgramProductDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZV23_1() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO2 =  new PromotionProgramDTO();
        promotionProgramDTO2.setId(1L);
        promotionProgramDTO2.setType("ZV23");
        promotionProgramDTO2.setGivenType(1);
        promotionProgramDTO2.setPromotionProgramCode("123");
        programs.add(promotionProgramDTO2);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        List<PromotionProgramProductDTO> programProduct = new ArrayList<>();
        PromotionProgramProductDTO promotionProgramProductDTO = new PromotionProgramProductDTO();
        promotionProgramProductDTO.setType(1);
        promotionProgramProductDTO.setProductId(1L);
        programProduct.add(promotionProgramProductDTO);
        PromotionProgramProductDTO promotionProgramProductDTO1 = new PromotionProgramProductDTO();
        promotionProgramProductDTO1.setType(2);
        promotionProgramProductDTO1.setProductId(2L);
        programProduct.add(promotionProgramProductDTO1);
        Response responsePromotionProgramProductDTO = new Response();
        responsePromotionProgramProductDTO.setData(programProduct);
        Mockito.when(promotionClient.findByPromotionIdsV1(Arrays.asList(1L))).thenReturn(responsePromotionProgramProductDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZM() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO3 =  new PromotionProgramDTO();
        promotionProgramDTO3.setId(1L);
        promotionProgramDTO3.setType("ZM");
        promotionProgramDTO3.setGivenType(1);
        programs.add(promotionProgramDTO3);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZM1() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO3 =  new PromotionProgramDTO();
        promotionProgramDTO3.setId(1L);
        promotionProgramDTO3.setType("ZM");
        promotionProgramDTO3.setGivenType(3);
        programs.add(promotionProgramDTO3);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOrderPromotions_ZM2() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/order-promotions";

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO3 =  new PromotionProgramDTO();
        promotionProgramDTO3.setId(1L);
        promotionProgramDTO3.setType("ZM");
        promotionProgramDTO3.setGivenType(2);
        programs.add(promotionProgramDTO3);
        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.findPromotionPrograms(shopId, Long.valueOf(orderPromotionRequest.getOrderType())
                , 1L, memberCard!=null?memberCard.getId():null, 1L, 1L)).thenReturn(responsePromotionProgramDTO);

        salePromotionServiceImpl.getSaleItemPromotions(orderPromotionRequest, shopId, null, false);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderPromotionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPromotionProduct() {
    }

    @Test
    public void promotionCalculation() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/promotion-calculation";

        SalePromotionCalculationRequest calculationRequest = new SalePromotionCalculationRequest();
        calculationRequest.setTotalOrderAmount(50000.0);
        List<SalePromotionCalItemRequest> promotionInfo = new ArrayList<>();
        SalePromotionCalItemRequest salePromotionCalItemRequest = new SalePromotionCalItemRequest();
        salePromotionCalItemRequest.setProgramId(1L);
        SalePromotionDiscountDTO amount = new SalePromotionDiscountDTO();
        amount.setAmount(6000.0);
        salePromotionCalItemRequest.setAmount(amount);
        promotionInfo.add(salePromotionCalItemRequest);
        calculationRequest.setPromotionInfo(promotionInfo);
        OrderPromotionRequest orderRequest = new OrderPromotionRequest();
        orderRequest.setCustomerId(1L);
        ProductOrderRequest dto = new ProductOrderRequest();
        dto.setQuantity(5);
        dto.setProductId(1L);
        dto.setProductCode("14563");

        List<ProductOrderRequest> products = new ArrayList<>();
        products.add(dto);
        orderRequest.setProducts(products);
        calculationRequest.setOrderRequest(orderRequest);

        List<PromotionProgramDTO> programs = new ArrayList<>();
        PromotionProgramDTO promotionProgramDTO1 =  new PromotionProgramDTO();
        promotionProgramDTO1.setId(1L);
        promotionProgramDTO1.setType("ZM");
        promotionProgramDTO1.setGivenType(2);
        programs.add(promotionProgramDTO1);

        PromotionProgramDTO promotionProgramDTO2 =  new PromotionProgramDTO();
        promotionProgramDTO2.setId(1L);
        promotionProgramDTO2.setType("ZM");
        promotionProgramDTO2.setGivenType(3);
        programs.add(promotionProgramDTO2);

        PromotionProgramDTO promotionProgramDTO3 =  new PromotionProgramDTO();
        promotionProgramDTO3.setId(1L);
        promotionProgramDTO3.setType("ZV01");
        promotionProgramDTO3.setGivenType(1);
        programs.add(promotionProgramDTO3);

        PromotionProgramDTO promotionProgramDTO4 =  new PromotionProgramDTO();
        promotionProgramDTO4.setId(1L);
        promotionProgramDTO4.setType("ZV07");
        promotionProgramDTO4.setGivenType(1);
        programs.add(promotionProgramDTO4);

        PromotionProgramDTO promotionProgramDTO5 =  new PromotionProgramDTO();
        promotionProgramDTO5.setId(1L);
        promotionProgramDTO5.setType("ZV13");
        promotionProgramDTO5.setGivenType(1);
        programs.add(promotionProgramDTO5);

        PromotionProgramDTO promotionProgramDTO6 =  new PromotionProgramDTO();
        promotionProgramDTO6.setId(1L);
        promotionProgramDTO6.setType("ZV19");
        promotionProgramDTO6.setGivenType(1);
        programs.add(promotionProgramDTO6);

        PromotionProgramDTO promotionProgramDTO7 =  new PromotionProgramDTO();
        promotionProgramDTO7.setId(1L);
        promotionProgramDTO7.setType("ZV23");
        promotionProgramDTO7.setGivenType(1);
        programs.add(promotionProgramDTO7);

        Response responsePromotionProgramDTO = new Response();
        responsePromotionProgramDTO.setData(programs);
        Mockito.when(promotionClient.getByIdsV1(Arrays.asList(1L))).thenReturn(responsePromotionProgramDTO);
        Mockito.when(customerTypeClient.getWarehouseTypeByShopId(shopId)).thenReturn(1L);

        salePromotionServiceImpl.promotionCalculation(calculationRequest, shopId);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(calculationRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPriceByPrID() {
    }

    @Test
    public void getDiscountCode() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/discount-code/123";

        orderRequest.setCustomerId(1L);
        orderRequest.setOrderType(1);
//        Mockito.when(saleOrderDiscountRepo.countDiscount(shopId,  1L, 1L, 1)).thenReturn(2);
//        Mockito.when(saleOrderDiscountRepo.countDiscount(shopId,  1L, 1L, 2)).thenReturn(1);
        Response response = new Response();
        response.setData(Collections.singleton(1L));
        Mockito.when(promotionClient.findCusCardPromotion(null, PromotionCustObjectType.CUSTOMER_TYPE.getValue())).thenReturn(response);
        Mockito.when(promotionClient.findCusCardPromotion(null, PromotionCustObjectType.MEMBER_CARD.getValue())).thenReturn(response);
        Mockito.when(promotionClient.findCusCardPromotion(null, PromotionCustObjectType.LOYAL_CUSTOMER.getValue())).thenReturn(response);
        Mockito.when(promotionClient.findCusCardPromotion(null, PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue())).thenReturn(response);
        salePromotionServiceImpl.getDiscountCode("123", shopId, orderRequest);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(orderRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getClientIp() {
    }
}
