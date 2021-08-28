package vn.viettel.authorization.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import vn.viettel.authorization.BaseTest;
import vn.viettel.authorization.service.TokenGenerateService;

public class TokenControllerTest extends BaseTest {

    @MockBean
    TokenGenerateService tokenGenerateService;

    @Test
    public void generateContinueToken() throws Exception{

    }

    @Test
    public void storeToken() throws Exception{
    }

    @Test
    public void getBlackListToken() throws Exception{
    }
}