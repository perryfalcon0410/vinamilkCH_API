package vn.viettel.authorization.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.dto.ShopDTO;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
//                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getByName() throws Exception{
        String uri = V1 + root;
    }

    @Test
    public void isEditableOnlineOrder() throws Exception{
        String uri = V1 + root;
    }

    @Test
    public void isManuallyCreatableOnlineOrder() throws Exception{
        String uri = V1 + root;
    }

    @Test
    public void dayReturn() throws Exception{
        String uri = V1 + root;
    }

    @Test
    public void getShopParam() throws Exception{
        String uri = V1 + root;
    }

    @Test
    public void updateShopParam() throws Exception{
        String uri = V1 + root;
    }
}