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
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.ChangePasswordRequest;
import vn.viettel.authorization.service.dto.LoginRequest;
import vn.viettel.authorization.service.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.dto.PermissionDTO;


import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class UserAuthenticateControllerTest extends BaseTest {
    private final String root = "/users";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @MockBean
    UserAuthenticateService userLoginService;

    @Test
    public void preLogin() throws Exception{
        String uri = V1 + root + "/preLogin";


        LoginRequest requestObj = new LoginRequest();
        requestObj.setUsername("admin");
        requestObj.setPassword("1234");
        requestObj.setRoleId(1L);
        requestObj.setShopId(1L);

        given( userLoginService.preLogin((LoginRequest) any())).willReturn(new Response<Object>());
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .header(headerType, secretKey)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void userLogin() throws Exception{
        String uri = V1 + root + "/login";
        LoginRequest requestObj = new LoginRequest();
        requestObj.setUsername("admin");
        requestObj.setPassword("1234");

        given( userLoginService.getRoleShop((LoginRequest) any())).willReturn(new Response<Object>());
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .header(headerType, secretKey)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void changePassword() throws Exception{
        String uri = V1 + root + "/change-password";

        ChangePasswordRequest requestObj = new ChangePasswordRequest();
        requestObj.setUsername("admin");
        requestObj.setOldPassword("1234");
        requestObj.setNewPassword("12345");
        requestObj.setConfirmPassword("12345");

        given( userLoginService.changePassword((ChangePasswordRequest) any())).willReturn(new Response<Object>());
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .header(headerType, secretKey)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
                MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getUserById() throws Exception{
        String uri = V1 + root + "/findById/{id}";
        given(userLoginService.getUserById(anyLong())).willReturn(new UserDTO());

        ResultActions resultActions = mockMvc.perform(get(uri,1)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));

    }

    @Test
    public void getShopByRole() throws Exception{
        String uri = V1 + root + "/get-shop-by-role/{roleId}";

        List<ShopDTO> lstDto = Arrays.asList(new ShopDTO(), new ShopDTO());
        given(userLoginService.getShopByRole(any())).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri,1)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

//    @Test
//    public void getUserPermission() throws Exception{
//        String uri = V1 + root + "/get-user-permission/{roleId}";
//        List<PermissionDTO> lstDto = Arrays.asList(new PermissionDTO(), new PermissionDTO());
//        given(userLoginService.getUserPermission(any())).willReturn(lstDto);
//
//        ResultActions resultActions = mockMvc.perform(get(uri,1)
//                .header(headerType, secretKey)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
////        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
}