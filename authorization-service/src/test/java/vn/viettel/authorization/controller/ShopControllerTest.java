package vn.viettel.authorization.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.authorization.entities.ShopParam;
import vn.viettel.authorization.repository.ShopParamRepository;
import vn.viettel.authorization.repository.ShopRepository;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.authorization.service.impl.ShopServiceImpl;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.ShopParamRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ShopControllerTest extends BaseTest {
    private final String root = "/users/shops";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    ShopServiceImpl shopService;

    @Mock
    ShopService service;

    @Mock
    ShopRepository repository;

    @Mock
    ShopParamRepository shopParamRepo;

    private List<Shop> lstEntities;

    private List<ShopParam> lstShopParamEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        shopService.setModelMapper(this.modelMapper);
        final ShopController controller = new ShopController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final Shop entity = new Shop();
            entity.setId((long) i);
            entity.setAddress("Address" + i);
            entity.setEmail("email." + i + "@email.com");
            entity.setPhone("012345678" + i);
            entity.setShopName("Shop" + i);
            entity.setShopCode("CODE"+ i);
            entity.setShopType("SHOP_TYPE_" + i);
            lstEntities.add(entity);
        }

        lstShopParamEntities = new ArrayList<>();
        final ShopParam shopParam = new ShopParam();
        shopParam.setId(1L);
        shopParam.setShopId(1L);
        shopParam.setCode("LIMITVC");
        shopParam.setType("SALEMT_LIMITVC");
        lstShopParamEntities.add(shopParam);
    }

    @Test
    public void getById() throws Exception{
        Long id = lstEntities.get(1).getId();
        String uri = V1 + root + "/" + id.toString();

        doReturn(Optional.of(lstEntities.get(1))).when(repository).findById(id);
        ShopDTO dto = shopService.getById(id);

        assertEquals(lstEntities.get(1).getId(), dto.getId());
        verify(repository).findById(id);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getByName() throws Exception{
        String shopName = lstEntities.get(1).getShopName();

        String uri = V1 + root;
        doReturn(lstEntities.get(1)).when(repository).findByShopName(shopName);
        ShopDTO dto = shopService.getByName(shopName);

        assertEquals(lstEntities.get(1).getShopName(), dto.getShopName());
        verify(repository).findByShopName(shopName);

        ResultActions resultActions = mockMvc.perform(get(uri).param("name",shopName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void isEditableOnlineOrderFalse() throws Exception{
        Long id = lstEntities.get(1).getId();
        String uri = V1 + root + "/editable/online-order/" + id.toString();

        assertEquals(false, shopService.isEditableOnlineOrder(id));

        ResultActions resultActions = mockMvc.perform(get(uri).header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void isEditableOnlineOrderTrue() throws Exception{
        Long shopId = lstEntities.get(0).getId();
        String uri = V1 + root + "/editable/online-order/" + shopId.toString();
        String type = "SALEMT_ONLINE_ORDER";
        String code = "VES_EDITING";

        Shop shop = new Shop();
        shop.setId(2L);
        shop.setStatus(1);
        shop.setParentShopId(2L);
        shop.setStatus(2);

        Mockito.when(repository.getById(shopId)).thenReturn(shop);
        Mockito.when(shopParamRepo.getShopParam(type, code, shopId)).thenReturn(Optional.empty());

        shopService.isEditableOnlineOrder(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri).header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void isManuallyCreatableOnlineOrderTestFalse() throws Exception{
        Long id = lstEntities.get(1).getId();
        String uri = V1 + root + "/manually-creatable/online-order/" + id.toString();

        assertEquals(false, shopService.isManuallyCreatableOnlineOrder(id));

        ResultActions resultActions = mockMvc.perform(get(uri).header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void isManuallyCreatableOnlineOrderTestTrue() throws Exception{
        Long shopId = lstEntities.get(1).getId();
        String uri = V1 + root + "/manually-creatable/online-order/" + shopId.toString();

        String type = "SALEMT_ONLINE_ORDER";
        String code = "MANUAL_ORDER";

        Shop shop = new Shop();
        shop.setId(2L);
        shop.setStatus(1);
        shop.setParentShopId(2L);
        shop.setStatus(2);

        Mockito.when(repository.getById(shopId)).thenReturn(shop);
        Mockito.when(shopParamRepo.getShopParam(type, code, shopId)).thenReturn(Optional.empty());

        shopService.isManuallyCreatableOnlineOrder(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri).header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void dayReturn() throws Exception{
        Long id = lstEntities.get(1).getId();
        String uri = V1 + root + "/day-return/" + id.toString();

        assertEquals("-1", shopService.dayReturn(id));

        ResultActions resultActions = mockMvc.perform(get(uri).header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getShopParam() throws Exception{
        Long id = lstEntities.get(0).getId();
        String uri = V1 + root + "/shop-params";

        Mockito.when(shopParamRepo.getShopParam("SALEMT_LIMITVC","LIMITVC", id))
                .thenReturn(Optional.of(lstShopParamEntities.get(0)));

        shopService.getShopParam("SALEMT_LIMITVC", "LIMITVC", id);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("shopId", id.toString())
                        .param("type","SALEMT_LIMITVC")
                        .param("code","LIMITVC")
                        .header(headerType, secretKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void updateShopParam() throws Exception{
        Long id = lstShopParamEntities.get(0).getId();
        String uri = V1 + root + "/shop-params-1/" + id.toString();

        ShopParamRequest requestObj = new ShopParamRequest();
        requestObj.setShopId("1");
        requestObj.setType("Auto");
        requestObj.setCode("ABC");
        requestObj.setName("Đà nẵng");
        requestObj.setDescription("hehe");
        requestObj.setStatus(1);


        ShopParamDTO dtoObj = new ShopParamDTO();
        dtoObj.setShopId(requestObj.getShopId());
        dtoObj.setType(requestObj.getType());
        dtoObj.setCode(requestObj.getCode());
        dtoObj.setName(requestObj.getName());
        dtoObj.setDescription(requestObj.getDescription());
        dtoObj.setStatus(requestObj.getStatus());

        Mockito.when(shopParamRepo.findById(id)).thenReturn(Optional.ofNullable(lstShopParamEntities.get(0)));

        assertEquals(lstShopParamEntities.get(0).getId(), shopService.updateShopParam(requestObj, id).getId());

        ResultActions resultActions = mockMvc.perform(put(uri)
                        .header(headerType, secretKey)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getByShopCode() throws Exception {
        String code = "code";
        String uri = V1 + root + "/code/" + code;

        Mockito.when(repository.findByShopCode(code)).thenReturn(new Shop());

        shopService.getByShopCode(code);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAllShopToRedInvoice() throws Exception {
        String code = "code";
        String uri = V1 + root + "/feign/shops" ;

        List<Long> shopIds = Arrays.asList(1L);

        Mockito.when(repository.getShopByIds(shopIds)).thenReturn(Arrays.asList(new Shop()));

        shopService.getAllShopToRedInvoice(shopIds);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getImportTransReturn() throws Exception {
        Long shopId = 1L;
        String uri = V1 + root + "/import-trans-return/" + shopId.toString();

        List<Long> shopIds = Arrays.asList(1L);
        String type = "SALEMT_LIMIT_DAY_RETURN";
        String code = "IMPORT_TRANS_RETURN";
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setStatus(1);
        shop.setParentShopId(2L);
        shop.setStatus(2);

        Mockito.when(repository.getById(shopId)).thenReturn(shop);
        Mockito.when(shopParamRepo.dayReturn(shopId, type, code)).thenReturn(null);
        Mockito.when(shopParamRepo.dayReturn(shop.getId(), type, code)).thenReturn(new ShopParam());

        shopService.getImportSaleReturn(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getLevelUpdateCustomer() throws Exception {
        Long shopId = 1L;
        String uri = V1 + root + "/level-customer" ;

        List<Long> shopIds = Arrays.asList(1L);
        String type = "SALEMT_EDIT_CUSTOMER";
        String code = "OTHER_STORE";
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setStatus(1);
        shop.setParentShopId(2L);
        shop.setStatus(2);

        Mockito.when(repository.getById(shopId)).thenReturn(shop);
        Mockito.when(shopParamRepo.dayReturn(shopId, type, code)).thenReturn(null);
        Mockito.when(shopParamRepo.dayReturn(shop.getId(), type, code)).thenReturn(new ShopParam());

        shopService.getLevelUpdateCustomer(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}