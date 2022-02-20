package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.impl.ExchangeTransReportServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExchangeTransControllerTest extends BaseTest {
    private final String root = "/reports/exchange-trans";

    @Spy
    @InjectMocks
    ExchangeTransReportServiceImpl exchangeTransReportService;

    @Mock
    CommonClient commonClient;

    @Mock
    ExchangeTransReportService service;

    private ExchangeTransFilter filter;

    private ExchangeTransReportDTO tableDynamicDTO;

    private List<Object[]> rowData;

    private List<CategoryDataDTO> reasonsDT;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ExchangeTransController controller = new ExchangeTransController();
        controller.setService(service);
        this.setupAction(controller);
        tableDynamicDTO = new ExchangeTransReportDTO();
        reasonsDT = new ArrayList<>();
        rowData = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            Object[] rowDatas = new Object[5];
            rowDatas[0] = "2021-05-01 22:20:20";
            rowDatas[1] = "PO00" + i;
            rowDatas[2] = "RED00"  + i;
            rowDatas[3] = LocalDateTime.now();
            rowDatas[4] = LocalDateTime.now();
            rowData.add(rowDatas);

            CategoryDataDTO dto  = new  CategoryDataDTO();
            dto.setCategoryCode("CODE00" + i);
            dto.setCategoryName("Tên là " + i);
            dto.setCategoryGroupCode("G_CODE00" + i);
            dto.setStatus(1);
            reasonsDT.add(dto);
        }

        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
        tableDynamicDTO.setResponse(rowData);

        filter = new ExchangeTransFilter();
        filter.setShopId(1L);
    }

    @Test
    public void getReportExchangeTrans() throws Exception{
        String uri = V1 + root;
        Pageable page = PageRequest.of(0, 10);
        doReturn(tableDynamicDTO).when(exchangeTransReportService).callProcedure(filter);
        doReturn(true).when(exchangeTransReportService).validMonth(filter);

        ExchangeTransReportDTO response =  exchangeTransReportService.getExchangeTransReport(filter, page);

        Page<Object[]> pageRes = (Page<Object[]>) response.getResponse();

        assertEquals(rowData.size(),pageRes.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void listReasonExchange() throws Exception{
        String uri = V1 + root + "/reason-exchange";
        Response  response = new Response<CategoryDataDTO>();
        response.setData(reasonsDT);
        doReturn(response).when(commonClient).getReasonExchangeV1();

        List<CategoryDataDTO> lst = exchangeTransReportService.listReasonExchange();
        assertEquals(lst.size(),reasonsDT.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}