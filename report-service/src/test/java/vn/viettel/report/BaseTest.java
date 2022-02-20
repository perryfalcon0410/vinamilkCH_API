package vn.viettel.report;


import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
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

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
//@EnableSpringDataWebSupport
public class BaseTest extends JsonObjectConverter {

    public final String V1 = "/api/v1";
    public final String V2 = "/api/v2";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Autowired
    protected MockMvc mockMvc;

    public Response shop;

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

        shop = new Response<ShopDTO>();
        ShopDTO dto = new ShopDTO();
        dto.setId(1L);
        dto.setShopCode("CH101");
        dto.setShopName("Cửa hàng số 1");
        dto.setMobiPhone("0983018998");
        dto.setPhone("0123018998");
        dto.setEmail("cuahang@gmail.com");
        shop.withData(dto);
        shop.setSuccess(true);

    }
}
