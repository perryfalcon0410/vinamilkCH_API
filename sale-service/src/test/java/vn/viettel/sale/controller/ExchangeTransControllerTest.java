package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.repository.ComboProductTransRepository;
import vn.viettel.sale.repository.ExchangeTransRepository;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.impl.ComboProductTransServiceImpl;
import vn.viettel.sale.service.impl.ExchangeTranServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ExchangeTransControllerTest extends BaseTest {
    private final String root = "/sales/exchangetrans";
    private final String uri = V1 + root;

    @InjectMocks
    ExchangeTranServiceImpl serviceImp;

    @Mock
    ExchangeTranService service;

    @Mock
    ExchangeTransRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ExchangeTransController controller = new ExchangeTransController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //-------------------------------getAllReason-------------------------------
    @Test
    public void getAllReasonTest() throws Exception {
        String url = uri + "/reasons";

        Response<List<CategoryDataDTO>> result = new Response<>();
        List<CategoryDataDTO> data = new ArrayList<>();
        data.add(new CategoryDataDTO());

        result.setData(data);

        given(service.getReasons()).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getAllExchangeTrans-------------------------------
    @Test
    public void getAllExchangeTransTest() throws Exception {
        CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> result =
                service.getAllExchange(any(), any(), any(), any(), any(), any());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------create-------------------------------
    @Test
    public void createTest() throws Exception {
        String url = uri + "/create";
        ExchangeTrans data = new ExchangeTrans();
        data.setCustomerId(1L);
        data.setTransCode("ABC");
        data.setStatus(1);
        data.setReasonId(6L);
        data.setShopId(1L);

        service.create(any(), any(), any());

        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(post(url)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getExchangeTrans-------------------------------
    @Test
    public void getExchangeTransTest() throws Exception {
        String url = uri + "/{id}";

        Response<ExchangeTransDTO> result = new Response<>();
        result.setData(new ExchangeTransDTO());

        given(service.getExchangeTrans(any())).willReturn(result.getData());

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------update-------------------------------
    @Test
    public void updateTest() throws Exception {
        String url = uri + "/update/{id}";

        ExchangeTrans data = new ExchangeTrans();
        data.setId(1L);
        data.setCustomerId(1L);
        data.setTransCode("ABC");
        data.setStatus(1);
        data.setReasonId(6L);
        data.setShopId(1L);

        service.update(any(), any(), any());
        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(put(url, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
