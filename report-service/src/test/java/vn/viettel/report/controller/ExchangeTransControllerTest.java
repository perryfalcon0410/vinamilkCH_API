package vn.viettel.report.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExchangeTransControllerTest extends BaseTest {

    @MockBean
    ExchangeTransReportService exchangeTransReportService;
    private final String root = "/reports/exchange-trans";

    @Test
    public void exportToExcel() {
    }

    @Test
    public void getReportExchangeTrans() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<ExchangeTransReportDTO> lstDto = Arrays.asList(new ExchangeTransReportDTO(), new ExchangeTransReportDTO());
        Page<ExchangeTransReportDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotalDTO> response = new CoverResponse<>(pageDto, new ExchangeTransTotalDTO());

        given(exchangeTransReportService.getExchangeTransReport(any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void listReasonExchange() throws Exception{
        String uri = V1 + root + "/reason-exchange";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO(), new CategoryDataDTO());

        given(exchangeTransReportService.listReasonExchange()).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }
}