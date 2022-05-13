package vn.viettel.common;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import vn.viettel.common.service.impl.ApParamServiceImpl;

import static org.junit.Assert.*;

public class CommonServiceApplicationTest extends BaseTest {

    @InjectMocks
    CommonServiceApplication commonServiceApplication;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void swaggerApiV1() {
        commonServiceApplication.swaggerApiV1();
    }

    @Test
    public void swaggerApiV2() {
        commonServiceApplication.swaggerApiV2();
    }
}