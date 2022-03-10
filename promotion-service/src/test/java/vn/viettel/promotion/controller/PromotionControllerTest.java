package vn.viettel.promotion.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.BaseTest;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.PromotionProgramService;
import vn.viettel.promotion.service.feign.ShopClient;
import vn.viettel.promotion.service.impl.PromotionProgramImpl;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(secure = false)
public class PromotionControllerTest extends BaseTest {

    private final String root = "/promotions";
    private final String uri = V1 + root;

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    private PromotionProgramImpl programService;

    @Mock
    PromotionProgramRepository repository;

    @Mock
    PromotionProgramService service;

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
        programService.setModelMapper(this.modelMapper);
        final PromotionController controller = new PromotionController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

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

        List<PromotionProgramDiscountDTO> promotionProgramDiscountDTOS = programService
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
        Long id = lstEntities.get(0).getId();
        String url = uri + "/" + id.toString();

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        PromotionProgramDTO promotionProgramDTO = programService.getPromotionProgramById(id);

        assertEquals(id, promotionProgramDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getRejectProduct-------------------------------
    @Test
    public void getRejectProductTestMissingBody() throws Exception {
        String url = uri + "/get-rejected-products";

        List<Long> list = programProducts.stream()
                .map(PromotionProgramProduct::getId)
                .collect(Collectors.toList());

        Mockito.when(promotionProductRepository.findByPromotionIds(list)).thenReturn(programProducts);

        List<PromotionProgramProductDTO> promotionProgramProductDTOS =
                programService.findByPromotionIds(list);

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
                programService.findByPromotionIds(list);

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


        PromotionShopMapDTO promotionShopMapDTO = programService.getPromotionShopMap(id, 1L);

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

        List<PromotionProductOpenDTO> promotionProductOpenDTOS = programService.getFreeItems(programId);

        assertEquals(promotionProductOpens.size(), promotionProductOpenDTOS.size());

        ResultActions resultActions = mockMvc.perform(post(url)
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
