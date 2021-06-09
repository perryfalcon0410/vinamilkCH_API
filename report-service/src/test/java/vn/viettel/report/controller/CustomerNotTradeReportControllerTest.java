package vn.viettel.report.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerNotTradeReportControllerTest extends BaseTest {
    private final String root = "/reports/customers";

    @MockBean
    CustomerNotTradeService customerNotTradeService;

    @Test
    public void getCustomerNotTrade() throws Exception{
        String uri = V1 + root + "/not-trade";

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<CustomerReportDTO> lstDto = Arrays.asList(new CustomerReportDTO(), new CustomerReportDTO());
        Page<CustomerReportDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        given(customerNotTradeService.index(any(), any(), any(), any())).willReturn(pageDto);
        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate", "01/04/2021")
                .param("toDate", "01/05/2021")
                .param("isPaging", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));

    }

    @Test
    public void exportToExcel() {
    }
}