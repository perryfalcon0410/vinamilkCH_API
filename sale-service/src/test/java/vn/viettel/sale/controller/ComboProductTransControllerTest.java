package vn.viettel.sale.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;

import java.util.Arrays;
import java.util.Date;
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
        Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> result = new Response<>();
        CoverResponse<Page<ComboProductTranDTO>, TotalResponse> data = new CoverResponse<>();
        List<ComboProductTranDTO> listData = Arrays.asList(new ComboProductTranDTO());

        data.setResponse(new PageImpl<>(listData));
        data.setInfo(new TotalResponse());

        result.setData(data);

//        given(comboProductTransService.getAll(any(), any())).willReturn(result);

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
        data.setTransDate(new Date());
        data.setTransType(1);

        ComboProductTranDetailRequest combo = new ComboProductTranDetailRequest();
        combo.setComboProductId(1L);
        combo.setPrice(10000F);
        combo.setQuantity(10);
        data.setDetails(Arrays.asList(combo));

        Response<ComboProductTranDTO> result = new Response<>();
        ComboProductTranDTO response = new ComboProductTranDTO();
        response.setTransCode("ABC");
        response.setShopId(1L);
        response.setWareHouseTypeId(1L);
        response.setTransDate(new Date());
        response.setTotalQuantity(10);
        response.setTotalAmount(10000F);

        result.setData(response);

        given(comboProductTransService.create(any(), any(), any())).willReturn(result);

        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getComboProductTran-------------------------------
    @Test
    public void getComboProductTranTest() throws Exception {
        String url = uri + "/{id}";

        Response<ComboProductTranDTO> result = new Response<>();
        result.setData(new ComboProductTranDTO());

        given(comboProductTransService.getComboProductTrans(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

}
