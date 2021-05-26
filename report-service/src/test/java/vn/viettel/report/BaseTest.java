package vn.viettel.report;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import vn.viettel.core.convert.JsonObjectConverter;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReportServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
