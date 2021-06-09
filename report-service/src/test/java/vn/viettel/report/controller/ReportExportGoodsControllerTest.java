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
import vn.viettel.report.messaging.PrintGoodFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportExportGoodsControllerTest extends BaseTest {
    private final String root = "/reports/export-goods";

    @MockBean
    ReportExportGoodsService reportExportGoodsService;

    @Test
    public void getAllExportGood() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<ExportGoodsDTO> lstDto = Arrays.asList(new ExportGoodsDTO(), new ExportGoodsDTO());
        Page<ExportGoodsDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<ExportGoodsDTO>, TotalReport> response = new CoverResponse<>(pageDto, new TotalReport());

        given(reportExportGoodsService.index(any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
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

    @Test
    public void getDataToPrint() throws Exception{
        String uri = V1 + root + "/print";
        CoverResponse<PrintGoodFilter, TotalReport> coverResponse = new CoverResponse<>(new PrintGoodFilter(), new TotalReport());

        given(reportExportGoodsService.getDataToPrint(any()))
                .willReturn(coverResponse);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }
}