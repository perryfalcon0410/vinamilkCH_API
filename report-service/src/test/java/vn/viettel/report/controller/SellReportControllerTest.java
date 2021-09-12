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
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.SellDTO;
import vn.viettel.report.service.dto.SellTotalDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellReportControllerTest extends BaseTest {
    private final String root = "/reports/sells";
    @MockBean
    SellsReportService sellsReportService;

    @Test
    public void getReportSells() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<SellDTO> lstDto = Arrays.asList(new SellDTO(), new SellDTO());
        Page<SellDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<SellDTO>, SellTotalDTO> response = new CoverResponse<>(pageDto, new SellTotalDTO());

        given(sellsReportService.getSellReport(any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate","2021/07/01")
                .param("toDate","2021/07/13")
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

    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/print";
        List<SellDTO> lstDto = Arrays.asList(new SellDTO(), new SellDTO());
        CoverResponse<List<SellDTO>, ReportDateDTO> response = new CoverResponse<>(lstDto, new ReportDateDTO());

        given(sellsReportService.getDataPrint(any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }
}