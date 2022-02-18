package vn.viettel.sale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.web.servlet.MockMvc;
import vn.viettel.core.convert.JsonObjectConverter;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class BaseTest extends JsonObjectConverter {

    public final String V1 = "/api/v1";
    public final String V2 = "/api/v2";

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void initTest() throws Exception {
        assertEquals("", "");
    }
}
