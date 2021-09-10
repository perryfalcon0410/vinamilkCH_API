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
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StockTotalReportControllerTest extends BaseTest {
    private final String root = "/reports/stock-total";
    @MockBean
    StockTotalReportService stockTotalReportService;

    @Test
    public void getStockTotalReport() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<StockTotalReportDTO> lstDto = Arrays.asList(new StockTotalReportDTO(), new StockTotalReportDTO());
        Page<StockTotalReportDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> response = new CoverResponse<>(pageDto, new StockTotalInfoDTO());

        given(stockTotalReportService.getStockTotalReport( any(), Mockito.any(PageRequest.class)))
                .willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("stockDate" , "01/05/2021")
                .param("warehouseTypeId", "1L")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"pageNumber\":" + page));
//        assertThat(responseData, containsString("\"pageSize\":" + size));

    }

    @Test
    public void exportToExcel() {
    }
}