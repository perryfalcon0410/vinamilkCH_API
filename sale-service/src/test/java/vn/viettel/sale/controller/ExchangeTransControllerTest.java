package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.repository.ExchangeTransRepository;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.impl.ExchangeTranServiceImpl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    @Mock
    CategoryDataClient categoryDataClient;

    private List<ExchangeTrans> exchangeTransList;

    private List<CategoryDataDTO> categoryDataDTOS;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ExchangeTransController controller = new ExchangeTransController();
        controller.setService(service);
        this.setupAction(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        exchangeTransList = new ArrayList<>();
        final ExchangeTrans exchangeTrans = new ExchangeTrans();
        exchangeTrans.setId(1L);
        exchangeTransList.add(exchangeTrans);

        categoryDataDTOS = new ArrayList<>();
        final CategoryDataDTO categoryDataDTO = new CategoryDataDTO();
        categoryDataDTO.setId(1L);
        categoryDataDTOS.add(categoryDataDTO);
    }

    //-------------------------------getAllReason-------------------------------
    @Test
    public void getAllReasonTest() throws Exception {
        String url = uri + "/reasons";

        Response<List<CategoryDataDTO>> result = serviceImp.getReasons();

        ResultActions resultActions = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getAllExchangeTrans-------------------------------
    @Test
    public void getAllExchangeTransTest() throws Exception {

        Date fromDate = new Date("2022/02/22");
        Date toDate = new Date("2022/02/22");

        Mockito.when(repository.getExchangeTrans(1L, "A", 1, 1L, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), PageRequest.of(1,5)))
                .thenReturn(new PageImpl<>(exchangeTransList));

        Mockito.when(categoryDataClient.getReasonExchangeFeign(Arrays.asList(-1L), null, null))
                .thenReturn(new Response<List<CategoryDataDTO>>().withData(categoryDataDTOS));

        CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> result =
                serviceImp.getAllExchange(1L, "A", fromDate, toDate, 1L, PageRequest.of(1,5));

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
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
