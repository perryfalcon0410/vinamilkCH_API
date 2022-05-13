package vn.viettel.authorization.controller;

import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.TokenGenerateService;
import vn.viettel.authorization.service.impl.TokenGenerateServiceImpl;
import vn.viettel.core.dto.ShopDTO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenControllerTest extends BaseTest {

    @InjectMocks
    TokenGenerateServiceImpl serviceImpl;

    @Mock
    TokenGenerateService service;

    @Mock
    JwtTokenCreate jwtTokenCreate;

    private final String root = "/token";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final TokenController controller = new TokenController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void generateContinueToken() throws Exception{

        String uri = V1 + root + "/feignGenerateContinueToken";

        DefaultClaims claims = new DefaultClaims();

        when(jwtTokenCreate.createToken(claims)).thenReturn("123456");

        serviceImpl.createToken(claims);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(claims))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void storeToken() throws Exception{
    }

    @Test
    public void getBlackListToken() throws Exception{
    }

    @Test
    public void testGenerateContinueToken() {
    }

    @Test
    public void testStoreToken() {
    }

    @Test
    public void testGetBlackListToken() {
    }
}