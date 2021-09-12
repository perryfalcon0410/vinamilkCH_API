package vn.viettel.report.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
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
        ExchangeTransReportDTO response = new ExchangeTransReportDTO();
        given(exchangeTransReportService.getExchangeTransReport(any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void listReasonExchange() throws Exception{
        String uri = V1 + root + "/reason-exchange";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO(), new CategoryDataDTO());

        given(exchangeTransReportService.listReasonExchange()).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }
}