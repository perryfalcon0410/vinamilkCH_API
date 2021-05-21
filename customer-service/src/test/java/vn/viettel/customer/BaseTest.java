package vn.viettel.customer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import vn.viettel.core.convert.JsonObjectConverter;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomerServiceApplication.class)
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
