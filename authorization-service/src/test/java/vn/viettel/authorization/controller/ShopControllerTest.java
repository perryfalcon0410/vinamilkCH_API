package vn.viettel.authorization.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.ShopParamRequest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ShopControllerTest extends BaseTest {
    private final String root = "/users/shops";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @MockBean
    ShopService shopService;

    @Test
    public void getById() throws Exception{
        String uri = V1 + root + "/{id}";


        given(shopService.getById(anyLong())).willReturn(new ShopDTO());

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getByName() throws Exception{
        String uri = V1 + root;
        given(shopService.getByName(any())).willReturn(new ShopDTO());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("name" , "Hải Dương")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void isEditableOnlineOrderFalse() throws Exception{
        String uri = V1 + root + "/editable/online-order/{shopId}";

        given(shopService.isEditableOnlineOrder(anyLong())).willReturn(false);

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void isEditableOnlineOrderTrue() throws Exception{
        String uri = V1 + root + "/editable/online-order/{shopId}";

        given(shopService.isEditableOnlineOrder(anyLong())).willReturn(true);

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void isManuallyCreatableOnlineOrderTestFalse() throws Exception{
        String uri = V1 + root + "/manually-creatable/online-order/{shopId}";
        given(shopService.isManuallyCreatableOnlineOrder(anyLong())).willReturn(false);

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

    }

    @Test
    public void isManuallyCreatableOnlineOrderTestTrue() throws Exception{
        String uri = V1 + root + "/manually-creatable/online-order/{shopId}";
        given(shopService.isManuallyCreatableOnlineOrder(anyLong())).willReturn(true);

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

    }

    @Test
    public void dayReturn() throws Exception{
        String uri = V1 + root + "/day-return/{shopId}";

        given(shopService.dayReturn(anyLong())).willReturn(new String());

        ResultActions resultActions = mockMvc.perform(get(uri,1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getShopParam() throws Exception{
        String uri = V1 + root + "/shop-params";

        given(shopService.getShopParam(any(),any(),anyLong())).willReturn(new ShopParamDTO());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("type","SALEMT_LIMITVC")
                .param("code","LIMITVC")
                .param("shopId","1")
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void updateShopParam() throws Exception{
        String uri = V1 + root + "/shop-params-1/{id}";

        ShopParamRequest requestObj = new ShopParamRequest();
        requestObj.setShopId("2");
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


        given( shopService.updateShopParam((ShopParamRequest) any(), any())).willReturn(dtoObj);
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri, 1)
                        .header(headerType, secretKey)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }
}