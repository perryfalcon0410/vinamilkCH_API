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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

public class ChangePriceReportControllerTest extends BaseTest {
    private final String root = "/reports/changePrices";

    @MockBean
    ChangePriceReportService changePriceReportService;

    @Test
    public void indexTestIsPagingTrue() throws Exception{
        String uri = V1 + root;

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<ChangePriceDTO> lstDto = Arrays.asList(new ChangePriceDTO(), new ChangePriceDTO());
        Page<ChangePriceDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO> response = new CoverResponse<>(pageDto, new ChangePriceTotalDTO());
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2021,4,1);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2021,5,30);
        given(changePriceReportService.index(any(),any(),any(),any(),any(),any(), any(),any())).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromTransDate" , "01/04/2021")
                .param("toTransDate", "01/05/2021")
                .param("fromOrderDate", "01/04/2021")
                .param("toOrderDate", "01/05/2021")
                .param("isPaging" , "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void indexTestIsPagingFalse() throws Exception{
        String uri = V1 + root;

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<ChangePriceDTO> lstDto = Arrays.asList(new ChangePriceDTO(), new ChangePriceDTO());
        Page<ChangePriceDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO> response = new CoverResponse<>(pageDto, new ChangePriceTotalDTO());
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2021,4,1);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2021,5,30);
        given(changePriceReportService.index(any(),any(),any(),any(),any(),any(), any(),any())).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromTransDate" , "01/04/2021")
                .param("toTransDate", "01/05/2021")
                .param("fromOrderDate", "01/04/2021")
                .param("toOrderDate", "01/05/2021")
                .param("isPaging" , "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void getAll() throws Exception{
        String uri = V1 + root + "/print";
        List<EntryMenuDetailsDTO> lstDto = Arrays.asList(new EntryMenuDetailsDTO(), new EntryMenuDetailsDTO());
        List<ChangePriceDTO> changePriceDTOS = Arrays.asList(new ChangePriceDTO(), new ChangePriceDTO());

//        given(changePriceReportService.getAll(any(),any(),any(),any(),any(),any(), any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromTransDate" , "01/04/2021")
                .param("toTransDate", "01/05/2021")
                .param("fromOrderDate", "01/04/2021")
                .param("toOrderDate", "01/05/2021")
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void exportToExcel() {
    }
}