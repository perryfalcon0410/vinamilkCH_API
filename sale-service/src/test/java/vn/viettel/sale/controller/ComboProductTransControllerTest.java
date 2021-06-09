package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.TotalDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ComboProductTransControllerTest extends BaseTest {
    private final String root = "/sales/combo-product-trans";
    private final String uri = V1 + root;

    @MockBean
    ComboProductTransService comboProductTransService;

    //-------------------------------findComboProductTrans-------------------------------
    @Test
    public void findComboProductTransTest() throws Exception {
        CoverResponse<Page<ComboProductTranDTO>, TotalDTO> data = new CoverResponse<>();
        List<ComboProductTranDTO> listData = Arrays.asList(new ComboProductTranDTO());
        data.setResponse(new PageImpl<>(listData));
        data.setInfo(new TotalDTO());

        given(comboProductTransService.getAll(any(), Mockito.any(PageRequest.class))).willReturn(data);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------create-------------------------------
    @Test
    public void createTest() throws Exception {

        ComboProductTranRequest data = new ComboProductTranRequest();
        data.setTransDate(LocalDateTime.now());
        data.setTransType(1);

        ComboProductTranDetailRequest combo = new ComboProductTranDetailRequest();
        combo.setComboProductId(1L);
        combo.setPrice(10000.0);
        combo.setQuantity(10);
        data.setDetails(Arrays.asList(combo));

        ComboProductTranDTO response = new ComboProductTranDTO();
        response.setTransCode("ABC");
        response.setShopId(1L);
        response.setWareHouseTypeId(1L);
        response.setTransDate(LocalDateTime.now());
        response.setTotalQuantity(10);
        response.setTotalAmount(10000.0);

        given(comboProductTransService.create(any(), any(), any())).willReturn(response);

        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getComboProductTran-------------------------------
    @Test
    public void getComboProductTranTest() throws Exception {
        String url = uri + "/{id}";

        ComboProductTranDTO result = new ComboProductTranDTO();

        given(comboProductTransService.getComboProductTrans(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

}
