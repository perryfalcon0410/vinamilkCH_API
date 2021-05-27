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
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntryMenuDetailsControllerTest extends BaseTest {
    private final String root = "/reports/entryMenuDetails";

    @MockBean
    private EntryMenuDetailsReportService service;

    @Test
    public void getReportReturnGoods() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<EntryMenuDetailsDTO> lstDto = Arrays.asList(new EntryMenuDetailsDTO(), new EntryMenuDetailsDTO());
        Page<EntryMenuDetailsDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<EntryMenuDetailsDTO>,ReportTotalDTO> response = new CoverResponse<>(pageDto, new ReportTotalDTO());

        given(service.getEntryMenuDetailsReport(any(), Mockito.any(PageRequest.class)))
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
    public void exportToExcel() throws Exception{

    }

    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/print";
        List<EntryMenuDetailsDTO> lstDto = Arrays.asList(new EntryMenuDetailsDTO(), new EntryMenuDetailsDTO());
        CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO> response = new CoverResponse<>(lstDto, new ReportDateDTO());

        given(service.getEntryMenuDetails(any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }
}