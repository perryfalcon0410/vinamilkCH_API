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
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InOutAdjustmentControllerTest extends BaseTest {
    private final String root = "/reports/in-out-adjustment";

    @MockBean
    InOutAdjustmentService inOutAdjustmentService;

    @Test
    public void find() throws Exception{

        String uri = V1 + root;

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        List<InOutAdjusmentDTO> lstDto = Arrays.asList(new InOutAdjusmentDTO(), new InOutAdjusmentDTO());
        Page<InOutAdjusmentDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
        given(inOutAdjustmentService.find(any(), Mockito.any(PageRequest.class))).willReturn(pageDto);
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