package vn.viettel.sale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import vn.viettel.core.convert.JsonObjectConverter;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class BaseTest extends JsonObjectConverter {

    public final String V1 = "/api/v1";
    public final String V2 = "/api/v2";

    @Autowired
    protected MockMvc mockMvc;

    public void setupAction(Object... controllers) {
        mockMvc = MockMvcBuilders.standaloneSetup(controllers)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers(new ViewResolver() {
                    @Override
                    public View resolveViewName(String viewName, Locale locale) throws Exception {
                        return new MappingJackson2JsonView();
                    }
                })
                .build();
    }

    @Test
    public void initTest() throws Exception {
        assertEquals("", "");
    }
}
