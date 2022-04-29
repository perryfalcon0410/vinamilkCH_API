package vn.viettel.promotion.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.promotion.BaseTest;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionCustAttrService;
import vn.viettel.promotion.service.PromotionItemProductService;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.RPT_ZV23Service;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;
import vn.viettel.promotion.service.impl.CustomerPromotionServiceImpl;
import vn.viettel.promotion.service.impl.PromotionItemProductImpl;
import vn.viettel.promotion.service.impl.PromotionProgramImpl;
import vn.viettel.promotion.service.impl.RPT_ZV23Impl;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PromotionControllerTest extends BaseTest {

    private final String root = "/promotions";
    private final String uri = V1 + root;

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    PromotionProgramImpl serviceImpl;
    @InjectMocks
    CustomerPromotionServiceImpl promotionCustAttrService;

    @InjectMocks
    RPT_ZV23Impl rpt_zv23Service;

    @Mock
    PromotionRPT_ZV23Repository rpt_zv23Repository;

    @InjectMocks
    PromotionItemProductImpl itemProductService;

    @Mock
    PromotionItemProductRepository itemProductServiceIml;

    @Mock
    PromotionProgramService service;

    @Mock
    PromotionProgramRepository repository;

    @Mock
    PromotionCustATTRDetailRepository rromotionCustATTRDetailRepository;

    @Mock
    PromotionProgramDiscountRepository promotionDiscountRepository;

    @Mock
    PromotionProgramProductRepository promotionProductRepository;

    @Mock
    ShopClient shopClient;

    @Mock
    PromotionShopMapRepository promotionShopMapRepository;

    @Mock
    PromotionProductOpenRepository promotionProductOpenRepository;

    @Mock
    PromotionProgramRepository promotionProgramRepository;
    @Mock
    PromotionSaleProductRepository promotionSaleProductRepository;
    @Mock
    PromotionCusAttrRepository promotionCusAttrRepository;
    @Mock
    PromotionProgramDetailRepository promotionDetailRepository;
    @Mock
    CustomerClient customerClient;
    @Mock
    PromotionCustATTRDetailRepository promotionCustATTRDetailRepository;

    @Mock
    EntityManager entityManager;

    private List<PromotionProgram> lstEntities;

    private List<PromotionProgramDiscount> promotionProgramDiscounts;

    private List<PromotionProgramProduct> programProducts;

    private List<ShopDTO> shopDTOS;

    private List<PromotionShopMap> promotionShopMaps;

    private List<PromotionProductOpen> promotionProductOpens;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImpl.setModelMapper(this.modelMapper);
        rpt_zv23Service.setModelMapper(this.modelMapper);
        promotionCustAttrService.setModelMapper(this.modelMapper);
        promotionCustAttrService.setRepository(rromotionCustATTRDetailRepository);
        final PromotionController controller = new PromotionController();
        controller.setService(service);
        this.setupAction(controller);

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final PromotionProgram entity = new PromotionProgram();
            entity.setId((long) i);
            lstEntities.add(entity);
        }

        promotionProgramDiscounts = new ArrayList<>();
        final PromotionProgramDiscount promotionProgramDiscount = new PromotionProgramDiscount();
        promotionProgramDiscount.setId(1L);
        promotionProgramDiscounts.add(promotionProgramDiscount);

        programProducts = new ArrayList<>();
        final PromotionProgramProduct programProduct = new PromotionProgramProduct();
        programProduct.setId(1L);
        programProducts.add(programProduct);

        shopDTOS = new ArrayList<>();
        final ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTOS.add(shopDTO);

        promotionShopMaps = new ArrayList<>();
        final PromotionShopMap promotionShopMap = new PromotionShopMap();
        promotionShopMap.setId(1L);
        promotionShopMap.setShopId(1L);
        promotionShopMap.setPromotionProgramId(1L);
        promotionShopMaps.add(promotionShopMap);

        promotionProductOpens = new ArrayList<>();
        final PromotionProductOpen promotionProductOpen = new PromotionProductOpen();
        promotionProductOpen.setId(1L);
        promotionProductOpen.setPromotionProgramId(1L);
        promotionProductOpens.add(promotionProductOpen);

    }

    //-------------------------------listPromotionProgramDiscountByOrderNumber-------------------------------
    @Test
    public void listPromotionProgramDiscountByOrderNumberTest() throws Exception {
        String orderNumber = "A01";
        String url = uri + "/promotion-program-discount/" + orderNumber;

        Mockito.when(promotionDiscountRepository.getPromotionProgramDiscountByOrderNumber(orderNumber))
                .thenReturn(promotionProgramDiscounts);

        List<PromotionProgramDiscountDTO> promotionProgramDiscountDTOS = serviceImpl
                .listPromotionProgramDiscountByOrderNumber(orderNumber);

        assertEquals(promotionProgramDiscounts.size(), promotionProgramDiscountDTOS.size());

        ResultActions resultActions = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getById-------------------------------
    @Test
    public void getByIdTest() throws Exception {
        long id = 1;
        String url = uri + "/" + id;
        PromotionProgram promotionProgram = new PromotionProgram();
        Mockito.when(promotionProgramRepository.findById(id)).thenReturn(Optional.of(promotionProgram));

        PromotionProgramDTO promotionProgramDTO = serviceImpl.getPromotionProgramById(id);

        assertNotNull(promotionProgramDTO);

        ResultActions resultActions = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //
    @Test
    public void getByIdsTest() throws Exception {
        String url = uri + "/ids";
        List<Long> promotionIds = lstEntities.stream().map(PromotionProgram::getId).collect(Collectors.toList());
        List<PromotionProgram> lstPromotionPrograms = new ArrayList<>();
        Mockito.when(repository.findAllById(promotionIds)).thenReturn(lstPromotionPrograms);
        List<PromotionProgramDTO> result = serviceImpl.findByIds(promotionIds);

        assertEquals(lstPromotionPrograms.size(), result.size());
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("programIds", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getRejectProduct-------------------------------
    @Test
    public void findByPromotionIds() throws Exception {
        String url = uri + "/get-rejected-products";

        List<Long> list = programProducts.stream()
                .map(PromotionProgramProduct::getId)
                .collect(Collectors.toList());

        Mockito.when(promotionProductRepository.findByPromotionIds(list)).thenReturn(programProducts);

        List<PromotionProgramProductDTO> promotionProgramProductDTOS =
                serviceImpl.findByPromotionIds(list);

        assertEquals(programProducts.size(), promotionProgramProductDTOS.size());

        ResultActions resultActions = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getRejectProductTest() throws Exception {
        String url = uri + "/get-rejected-products";

        List<Long> list = programProducts.stream()
                .map(PromotionProgramProduct::getId)
                .collect(Collectors.toList());

        Mockito.when(promotionProductRepository.findByPromotionIds(list)).thenReturn(programProducts);

        List<PromotionProgramProductDTO> promotionProgramProductDTOS =
                serviceImpl.findByPromotionIds(list);

        assertEquals(programProducts.size(), promotionProgramProductDTOS.size());

        ResultActions resultActions = mockMvc.perform(get(url)
                        .header(headerType, secretKey)
                        .param("ids", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getPromotionShopMap-------------------------------
    @Test
    public void getPromotionShopMapTest() throws Exception {
        Long id = lstEntities.get(0).getId();
        String url = uri + "/get-promotion-shop-map";

        Mockito.when(shopClient.getByIdV1(1L))
                .thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(promotionShopMapRepository.findByPromotionProgramIdAndShopId(id, 1L))
                .thenReturn(promotionShopMaps);


        PromotionShopMapDTO promotionShopMapDTO = serviceImpl.getPromotionShopMap(id, 1L);

        assertEquals(id, promotionShopMapDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(url)
                        .header(headerType, secretKey)
                        .param("promotionProgramId", "1")
                        .param("shopId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getFreeItem-------------------------------
    @Test
    public void getFreeItemTest() throws Exception {
        Long programId = 1L;
        String url = uri + "/get-free-items/" + programId.toString();

        Mockito.when(promotionProductOpenRepository.findByPromotionProgramId(programId))
                .thenReturn(promotionProductOpens);

        List<PromotionProductOpenDTO> promotionProductOpenDTOS = serviceImpl.getFreeItems(programId);

        assertEquals(promotionProductOpens.size(), promotionProductOpenDTOS.size());

        ResultActions resultActions = mockMvc.perform(post(url)
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void updatePromotionShopMap() throws Exception {
        Long id = 1L;
        String url = uri + "/promotion-shop-map";

        PromotionShopMapDTO shopMap = new PromotionShopMapDTO();
        shopMap.setId(1L);
        shopMap.setQuantityAdd(2.0);
        PromotionShopMap promotionShopMap = new PromotionShopMap();
        promotionShopMap.setQuantityMax(6.0);

        Mockito.when(promotionShopMapRepository.findById(shopMap.getId()))
                .thenReturn(Optional.of(promotionShopMap));

        Mockito.when(promotionShopMapRepository.save(promotionShopMap)).thenReturn(new PromotionShopMap());

        PromotionShopMapDTO result = serviceImpl.updatePromotionShopMap(shopMap);

        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getPromotionDiscount() throws Exception {
        Long shopId = 1L;
        String discountCode = "456987";
        String url = uri + "/promotion-program-discount/code/" + discountCode;

        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setParentShopId(1L);
        LocalDateTime firstDay = DateUtils.convertFromDate(LocalDateTime.now());
        LocalDateTime lastDay = DateUtils.convertToDate(LocalDateTime.now());

        Response response1 = new Response<>();
        response1.setData(shopDTO);
        Mockito.when(shopClient.getByIdV1(shopId))
                .thenReturn(response1);

        PromotionProgramDiscount discount = new PromotionProgramDiscount();
        discount.setPromotionProgramId(1L);

        Mockito.when(promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopId, firstDay, lastDay)).thenReturn(null);
        Mockito.when(promotionDiscountRepository.getPromotionProgramDiscount(discountCode, shopDTO.getParentShopId(), firstDay, lastDay)).thenReturn(Optional.of(discount));
        Mockito.when(promotionProgramRepository.getById(discount.getPromotionProgramId())).thenReturn(new PromotionProgram());

        PromotionProgramDiscountDTO response = serviceImpl.getPromotionDiscount(discountCode, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void isReturn() throws Exception {
        Long shopId = 1L;
        String code = "456";
        String url = uri + "/isReturn" ;

        PromotionProgram program = new PromotionProgram();
        program.setId(1L);
        program.setIsReturn(1);

        Mockito.when(promotionProgramRepository.findByCode(code)).thenReturn(program);

        Boolean response = serviceImpl.isReturn(code);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findPromotionPrograms() throws Exception {
        Long id = 1L;
        String code = "456";
        String url = uri + "/promotion-programs/shop/" + id.toString() ;

        Long orderType = 1L;
        Long customerTypeId = 1L;
        Long memberCardId = 1L;
        Long cusCloselyTypeId = 1L;
        Long cusCardTypeId = 1L;

        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setParentShopId(1L);
        Response response1 = new Response<>();
        response1.setData(shopDTO);
        Mockito.when(shopClient.getByIdV1(id))
                .thenReturn(response1);

        List<Long> lstShopId = Arrays.asList(1L, 1L);
        Mockito.when(promotionProgramRepository.findProgramWithConditions(lstShopId, orderType, customerTypeId, memberCardId, cusCloselyTypeId
                , cusCardTypeId, DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now()))).thenReturn(new ArrayList<>());

        List<PromotionProgramDTO> response = serviceImpl.findPromotionPrograms(id, orderType, customerTypeId, memberCardId, cusCloselyTypeId
                , cusCardTypeId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCusCardPromotion() throws Exception {
        Long programId = 1L;
        String url = uri + "/promotion-cust-attr/" + programId.toString() ;

        Integer objectType = 1;

        Set<Long> cusCards = new HashSet<>();
        Mockito.when(rromotionCustATTRDetailRepository.getPromotionCustATTRDetail(programId, objectType)).thenReturn(cusCards);

        Set<Long> response = promotionCustAttrService.getListCusCard(programId, objectType);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findPromotionProgramDetail() throws Exception {
        Long programId = 1L;
        String url = uri + "/promotion-program-detail/" + programId.toString() ;
        List<Long> productIds = Arrays.asList(1L);
        Mockito.when(promotionDetailRepository.findByPromotionProgramIdOrderByFreeQtyDesc(programId, productIds)).thenReturn(new ArrayList<>());

        List<PromotionProgramDetailDTO> response = serviceImpl.findPromotionDetailByProgramId(programId, productIds);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findPromotionDiscountByPromotion() throws Exception {
        Long programId = 1L;
        String url = uri + "/promotion-discount/" + programId.toString() ;
        List<Long> productIds = Arrays.asList(1L);
        Mockito.when(promotionDiscountRepository.findPromotionDiscountByPromotion(programId)).thenReturn(new ArrayList<>());

        List<PromotionProgramDiscountDTO> response = serviceImpl.findPromotionDiscountByPromotion(programId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void checkZV23Require() throws Exception {
        Long programId = 1L;
        String url = uri + "/RPT-ZV23/promotion-checkZV23";
         String promotionCode = "123";
         Long customerId = 1L;
         Long shopId = 1L;
        List<RPT_ZV23> rpt_zv23 = Arrays.asList(new RPT_ZV23());
        Mockito.when(rpt_zv23Repository.checkZV23Require(promotionCode, customerId)).thenReturn(rpt_zv23);

        RPT_ZV23DTO response = rpt_zv23Service.checkSaleOrderZV23(promotionCode, customerId, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findByProgramIds() throws Exception {
        Long programId = 1L;
        String url = uri + "/RPT-ZV23/promotion-checkZV23";
        Set<Long> programIds = new HashSet<>();
        Long customerId = 1L;
        Long shopId = 1L;
        List<RPT_ZV23> rpt_zv23 = Arrays.asList(new RPT_ZV23());
        Mockito.when(rpt_zv23Repository.getByProgramIds(programIds, customerId)).thenReturn(rpt_zv23);

        List<RPT_ZV23DTO> response = rpt_zv23Service.findByProgramIds(programIds, customerId, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void updateRPTZV23() throws Exception {
        Long id = 1L;
        String url = uri + "/RPT-ZV23/" + id.toString();
        RPT_ZV23Request request = new RPT_ZV23Request();
        Set<Long> programIds = new HashSet<>();
        Long customerId = 1L;
        Long shopId = 1L;
        List<RPT_ZV23> rpt_zv23 = Arrays.asList(new RPT_ZV23());
        Mockito.when(rpt_zv23Repository.findById(id)).thenReturn(Optional.of(new RPT_ZV23()));

        Boolean response = rpt_zv23Service.updateRPT_ZV23(id, request);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getProductsNotAccumulated() throws Exception {
        Long id = 1L;
        String url = uri + "/promotion-item-product/not-accumlated" ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        List<Long> productIds = Arrays.asList(1L);
        Mockito.when(itemProductServiceIml.productsNotAccumulated(productIds)).thenReturn(productIds);

        List<Long> response = itemProductService.listProductsNotAccumulated(productIds);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createRPTZV23() throws Exception {
        Long id = 1L;
        String url = uri + "/create/RPT-ZV23" ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        List<Long> productIds = Arrays.asList(1L);

        Boolean response = rpt_zv23Service.createRPT_ZV23(request);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findPromotionSaleProductByProgramId() throws Exception {
        Long programId = 1L;
        String url = uri + "/promotion-sale-product/" + programId.toString() ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        List<Long> productIds = Arrays.asList(1L);
        List<PromotionSaleProduct> saleProducts = new ArrayList<>();
        PromotionSaleProduct promotionSaleProduct = new PromotionSaleProduct();
        saleProducts.add(promotionSaleProduct);
        Mockito.when(promotionSaleProductRepository.findByPromotionProgramIdAndStatus(programId, 1)).thenReturn(saleProducts);

        List<PromotionSaleProductDTO> response = serviceImpl.findPromotionSaleProductByProgramId(programId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void checkSaleProductByProgramId() throws Exception {
        Long programId = 1L;
        String url = uri + "/check/promotion-sale-product/" + programId.toString() ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        List<Long> productIds = Arrays.asList(1L);
        List<PromotionSaleProduct> saleProducts = new ArrayList<>();
        PromotionSaleProduct promotionSaleProduct = new PromotionSaleProduct();
        saleProducts.add(promotionSaleProduct);
        Mockito.when(promotionSaleProductRepository.countDetail(programId)).thenReturn(1);
        Mockito.when(promotionSaleProductRepository.findByPromotionProgramIdAndStatus(programId, productIds)).thenReturn(2);

        Boolean response = serviceImpl.checkPromotionSaleProduct(programId, productIds);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void returnMGG() throws Exception {
        Long programId = 1L;
        String url = uri + "/mgg/return" ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        String orderCode = "1456";
        List<Long> productIds = Arrays.asList(1L);
        List<PromotionProgramDiscount> discounts = new ArrayList<>();
        PromotionProgramDiscount discount = new PromotionProgramDiscount();
        discount.setActualDiscountAmount(1.0);
        discount.setPromotionProgramId(1L);
        discounts.add(discount);
        List<PromotionShopMap> shopMapDB = new ArrayList<>();
        PromotionShopMap promotionShopMap = new PromotionShopMap();
        promotionShopMap.setQuantityReceived(5.0);
        shopMapDB.add(promotionShopMap);
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setParentShopId(1L);
        Response response1 = new Response<>();
        response1.setData(shopDTO);
        Mockito.when(shopClient.getByIdV1(shopId))
                .thenReturn(response1);

        Mockito.when(promotionDiscountRepository.getPromotionProgramDiscountByOrderNumber(orderCode)).thenReturn(discounts);
        Mockito.when(promotionShopMapRepository.findByPromotionProgramIdAndShopId(discount.getPromotionProgramId(), shopDTO.getId())).thenReturn(new ArrayList<>());
        Mockito.when(promotionShopMapRepository.findByPromotionProgramIdAndShopId(discount.getPromotionProgramId(), shopDTO.getParentShopId())).thenReturn(shopMapDB);

        List<Long> response = serviceImpl.returnMGG(orderCode, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void returnPromotionShopmap() throws Exception {
        Long programId = 1L;
        String url = uri + "/promotion-shop-map/return"  ;
        Map<String, Double> shopMaps = new HashMap<>();
        shopMaps.put("KEY",1.0);
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setParentShopId(1L);
        Response response1 = new Response<>();
        response1.setData(shopDTO);
        Mockito.when(shopClient.getByIdV1(shopId))
                .thenReturn(response1);
        List<Long> productIds = Arrays.asList(1L);
        List<PromotionSaleProduct> saleProducts = new ArrayList<>();
        PromotionSaleProduct promotionSaleProduct = new PromotionSaleProduct();
        saleProducts.add(promotionSaleProduct);
        PromotionShopMap promotionShopMap = new PromotionShopMap();
        promotionShopMap.setQuantityReceived(5.0);
        Mockito.when(promotionShopMapRepository.findByPromotionProgramCode("KEY", shopDTO.getId())).thenReturn(promotionShopMap);
//        Mockito.when(promotionSaleProductRepository.findByPromotionProgramIdAndStatus(programId, productIds)).thenReturn(2);

        List<Long> response = serviceImpl.returnPromotionShopmap(shopMaps, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getByCode() throws Exception {
        Long programId = 1L;
        String url = uri + "/program/123" ;
        RPT_ZV23Request request = new RPT_ZV23Request();
        Long customerId = 1L;
        Long shopId = 1L;
        List<Long> productIds = Arrays.asList(1L);
        List<PromotionSaleProduct> saleProducts = new ArrayList<>();
        PromotionSaleProduct promotionSaleProduct = new PromotionSaleProduct();
        saleProducts.add(promotionSaleProduct);
        PromotionProgram program = new PromotionProgram();
        Mockito.when(repository.findByCodeAndStatus("123", 1)).thenReturn(program);

        PromotionProgramDTO response = serviceImpl.getPromotionProgram("123");

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(post(url)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
